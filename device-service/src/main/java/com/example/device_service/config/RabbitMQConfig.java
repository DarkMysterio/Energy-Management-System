package com.example.device_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_TO_DEVICE_QUEUE = "user.to.device.queue";
    public static final String DEVICE_SYNC_QUEUE = "device.sync.queue";

    @Bean
    public Queue userToDeviceQueue() {
        return new Queue(USER_TO_DEVICE_QUEUE, true);
    }

    @Bean
    public Queue deviceSyncQueue() {
        return new Queue(DEVICE_SYNC_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
