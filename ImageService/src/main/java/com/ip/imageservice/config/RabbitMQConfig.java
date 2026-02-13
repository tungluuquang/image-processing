package com.ip.imageservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SYNC_QUEUE = "image.sync.queue";
    public static final String SYNC_EXCHANGE = "image.sync.exchange";
    public static final String SYNC_ROUTING_KEY = "image.save";

    @Bean
    public Queue syncQueue() {
        return new Queue(SYNC_QUEUE, true);
    }

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Binding syncBinding(Queue syncQueue, DirectExchange syncExchange) {
        return BindingBuilder.bind(syncQueue).to(syncExchange).with(SYNC_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}