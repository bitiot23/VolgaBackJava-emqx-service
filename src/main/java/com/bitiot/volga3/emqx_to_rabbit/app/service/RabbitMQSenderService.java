package com.bitiot.volga3.emqx_to_rabbit.app.service;

import com.bitiot.volga3.emqx_to_rabbit.app.model.CameraData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMQSenderService {

    private final RabbitTemplate rabbitTemplate;

    // Se inyectan los nombres desde application.yml
    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    // Spring inyectará el RabbitTemplate ya configurado con el MessageConverter
    public RabbitMQSenderService(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCameraData(CameraData data){
        try {
            log.debug("Enviando datos a RabbitMQ [Exchange: {}, RoutingKey: {}]: {}", exchangeName, routingKey, data);

            // RabbitTemplate se encargará de la conversión a JSON automáticamente.
            rabbitTemplate.convertAndSend(exchangeName, routingKey, data);

            log.info("Datos de la cámara {} enviados exitosamente a RabbitMQ.", data.getNameCamera());
        }catch (Exception e){
            log.error("Error al enviar datos a Rabbit para la cámara {}: ", data.getNameCamera(), e);
        }
    }
}
