package com.bitiot.volga3.emqx_to_rabbit.app.service;

import com.bitiot.volga3.emqx_to_rabbit.app.model.CameraData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MqttMessageProcessor {

    private final ObjectMapper objectMapper;
    private final RabbitMQSenderService rabbitMQSenderService;

    public MqttMessageProcessor(ObjectMapper objectMapper, RabbitMQSenderService rabbitMQSenderService) {
        this.objectMapper = objectMapper;
        this.rabbitMQSenderService = rabbitMQSenderService;
    }

    /**
     * La anotación @ServiceActivator le dice a Spring Integration que cada mensaje
     * que llegue al canal 'mqttInputChannel' debe ser procesado por este método.
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<String> message){
        String payload = message.getPayload();
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        log.info("Mensaje recibido del tópico [{}]: {}", topic, payload);

        try {
            // Convertimos el payload (JSON) a nuestro objeto CameraData
            CameraData cameraData = objectMapper.readValue(payload, CameraData.class);

            // Extraemos el nombre de la cámara del tópico (ejemplo de enriquecimiento)
            if (topic != null && cameraData.getNameCamera() == null){
                String[] topicParts = topic.split("/");
                cameraData.setNameCamera(topicParts[topicParts.length-1]);
            }

            // Enviamos los datos a Rabbit
            rabbitMQSenderService.sendCameraData(cameraData);

        }catch (IOException e){
            log.error("Error al deserializar el mensaje de MQTT: {}", payload, e);
        }
    }
}
