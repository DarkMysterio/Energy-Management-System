package com.example.monitoring_service.service;

import com.example.monitoring_service.dto.MeasurementMessage;
import com.example.monitoring_service.entity.HourlyConsumption;
import com.example.monitoring_service.repository.DeviceRepository;
import com.example.monitoring_service.repository.HourlyConsumptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AggregationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregationService.class);

    private final HourlyConsumptionRepository repository;
    private final DeviceRepository deviceRepository;

    public AggregationService(HourlyConsumptionRepository repository, DeviceRepository deviceRepository) {
        this.repository = repository;
        this.deviceRepository = deviceRepository;
    }

    public void addMeasurement(MeasurementMessage msg) {
        UUID deviceId = msg.getDeviceId();

        if (!deviceRepository.existsById(deviceId)) {
            LOGGER.warn("Ignoring measurement for unknown device: {}", deviceId);
            return;
        }

        Instant hourStart = msg.getTimestamp().truncatedTo(ChronoUnit.HOURS);

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
        LOGGER.info("Added measurement for device {}: {} kWh (total for hour: {} kWh)", 
                deviceId, msg.getMeasurementValue(), hc.getTotalKwh());
    }
}