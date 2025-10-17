package com.bitiot.volga3.emqx_to_rabbit.app.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
public class MqttInboundConfig {

    private final MqttProperties mqttProperties;

    public MqttInboundConfig(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    /**
     * Paso 1: Creamos la fábrica de clientes MQTT.
     * Spring Integration usará esto para crear y configurar el cliente Paho por nosotros.
     */

    @Bean
    public MqttPahoClientFactory mqttClientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttProperties.getBrokerUrl()});
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setAutomaticReconnect(true); //Reconexión automática
        options.setCleanSession(false); //Mantiene la sesión activa entre reconexiones
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * Creamos el canal de entrada.
     * Es como una tubería por donde fluirán los mensajes de MQTT hacia nuestra aplicación.
     */
    @Bean
    public MessageChannel mqttInputChannel(){
        return new DirectChannel();
    }

    /**
     * Creamos el adaptador de entrada (el que escucha mensajes).
     * Este componente se conecta, se suscribe y escucha los tópicos.
     * Cuando llega un mensaje, lo pone en el canal 'mqttInputChannel'.
     */
    @Bean
    public MessageProducer inbound(){
        // Usamos el wildcard '#' para suscribirnos a todos los tópicos bajo esa ruta.
        // ¡Mucho más mantenible que una lista!
        String topicToSubscribe = mqttProperties.getTopic();

        log.info("Suscribiéndose al tópico: {}", topicToSubscribe);

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        mqttProperties.getClientId() + "_inbound",
                        mqttClientFactory(),
                        topicToSubscribe
                );
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1); //Calidad de servicio: al menos una vez
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
}
