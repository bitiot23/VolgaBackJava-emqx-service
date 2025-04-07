package com.bitiot.volga3.emqx_to_rabbit.app.service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MqttCoordinator {// Coordina la lógica de suscripción para evitar las referencias circulares entre MqttService y MqttConnectionService

    private final MqttConnectionService mqttConnectionService;
    private final MqttService mqttService;

    public MqttCoordinator(MqttConnectionService mqttConnectionService, MqttService mqttService) {
        this.mqttConnectionService = mqttConnectionService;
        this.mqttService = mqttService;
    }

    @PostConstruct
    public void initialize(){
        //log.info("Ingresando al initialize");
        if (mqttConnectionService.connect()){ //Verifica si la conexión fue exitosa
            log.info("Conexión a MQTT exitosa. Suscribiendo a los tópicos...");
            mqttService.subscribeToTopics();

        } else {
            log.error("No se pudo conectar al broker MQTT. La suscripción al tópico no se realizará.");
        }


    }
}