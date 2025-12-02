package com.example.monitoring_service.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @Column(nullable = false)
    private UUID deviceId;

    @Column(name = "name")
    private String name;

    @Column(name = "max_consumption")
    private Double maxConsumption;

    public Device() {}

    public Device(UUID deviceId, String name, Double maxConsumption) {
        this.deviceId = deviceId;
        this.name = name;
        this.maxConsumption = maxConsumption;
    }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }
}
