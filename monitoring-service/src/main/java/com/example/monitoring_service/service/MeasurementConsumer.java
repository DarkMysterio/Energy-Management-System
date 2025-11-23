package com.example.monitoring_service.service;

import com.example.monitoring_service.config.RabbitConfig;
import com.example.monitoring_service.dto.MeasurementMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class MeasurementConsumer {

    private final AggregationService aggregationService;
    private final ObjectMapper objectMapper;


    public MeasurementConsumer(AggregationService aggregationService, ObjectMapper objectMapper) {
        this.aggregationService = aggregationService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.DATA_QUEUE)
    public void handleMeasurement(String messageJson) {
        MeasurementMessage msg =
                objectMapper.readValue(messageJson, MeasurementMessage.class);
        aggregationService.addMeasurement(msg);
    }
}

