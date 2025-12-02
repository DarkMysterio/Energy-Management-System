package com.example.device_service.dto;

import java.io.Serializable;

public class DeviceSyncMessage implements Serializable {
    private String operation; // CREATE, UPDATE, DELETE
    private String deviceId;
    private String name;
    private Double consumption;

    public DeviceSyncMessage() {}

    public DeviceSyncMessage(String operation, String deviceId, String name, Double consumption) {
        this.operation = operation;
        this.deviceId = deviceId;
        this.name = name;
        this.consumption = consumption;
    }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getConsumption() { return consumption; }
    public void setConsumption(Double consumption) { this.consumption = consumption; }
}
