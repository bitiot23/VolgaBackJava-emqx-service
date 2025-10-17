package com.bitiot.volga3.emqx_to_rabbit.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //Definimos una cola, un intercambio (exchange) y una clave de enrutamiento (routing key).
    public static final String QUEUE_NAME = "camara-queue";
    public static final String EXCHANGE_NAME = "exchange-camera";
    public static final String ROUTING_KEY = "camara-routing-key";

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public TopicExchange exchange(){//Usamos un TopicExchange para mayor flexibilidad.
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper){
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
