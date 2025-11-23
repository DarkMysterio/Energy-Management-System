package com.example.monitoring_service.service;

import com.example.monitoring_service.dto.MeasurementMessage;
import com.example.monitoring_service.entity.HourlyConsumption;
import com.example.monitoring_service.repository.HourlyConsumptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AggregationService {

    private final HourlyConsumptionRepository repository;

    public AggregationService(HourlyConsumptionRepository repository) {
        this.repository = repository;
    }

    public void addMeasurement(MeasurementMessage msg) {
        Instant hourStart = msg.getTimestamp().truncatedTo(ChronoUnit.HOURS);
        UUID deviceId = msg.getDeviceId();

        HourlyConsumption hc = repository
                .findByDeviceIdAndHourStart(deviceId, hourStart)
                .orElse(null);

        if (hc == null) {
            hc = new HourlyConsumption();
            hc.setDeviceId(deviceId);
            hc.setHourStart(hourStart);
            hc.setTotalKwh(0.0);
        }

        hc.setTotalKwh(hc.getTotalKwh() + msg.getMeasurementValue());
        repository.save(hc);
    }
}