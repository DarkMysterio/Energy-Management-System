package com.example.monitoring_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "hourly_consumption",
        uniqueConstraints = @UniqueConstraint(columnNames = {"device_id", "hour_start"}))
public class HourlyConsumption {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "hour_start", nullable = false)
    private Instant hourStart;

    @Column(name = "total_kwh", nullable = false)
    private double totalKwh;

}
