package com.example.data_simulator.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${simulator.queue-name}")
    private String queueName;

    @Bean
    public Queue deviceDataQueue() {
        return new Queue(queueName, true);
    }
}
