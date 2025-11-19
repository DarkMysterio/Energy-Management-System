package com.example.monitoring_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class RabbitConfig {
    public  static final String DATA_QUEUE = "device.data.queue";

    @Bean
    public Queue dataQueue() {
        return new Queue(DATA_QUEUE, true); // durable
    }
}
