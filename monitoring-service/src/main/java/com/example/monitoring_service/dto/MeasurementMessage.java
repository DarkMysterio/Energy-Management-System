package com.example.monitoring_service.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class MeasurementMessage {
    private Instant timestamp;
    private UUID deviceId;
    private double measurementValue;
}
