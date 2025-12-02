package com.example.monitoring_service.service;

import com.example.monitoring_service.config.RabbitConfig;
import com.example.monitoring_service.dto.MeasurementMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MeasurementConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementConsumer.class);

    private final AggregationService aggregationService;
    private final ObjectMapper objectMapper;


    public MeasurementConsumer(AggregationService aggregationService, ObjectMapper objectMapper) {
        this.aggregationService = aggregationService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.DATA_QUEUE)
    public void handleMeasurement(String messageJson) {
        try {
            MeasurementMessage msg = objectMapper.readValue(messageJson, MeasurementMessage.class);
            aggregationService.addMeasurement(msg);
        } catch (Exception e) {
            LOGGER.error("Failed to process measurement message: {}", e.getMessage());
        }
    }
}

