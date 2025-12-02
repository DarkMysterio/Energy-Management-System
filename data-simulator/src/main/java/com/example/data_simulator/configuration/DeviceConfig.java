package com.example.data_simulator.configuration;

/**
 * Represents a single device configuration for simulation
 */
public class DeviceConfig {
    private String deviceId;
    private long intervalMillis;

    public DeviceConfig() {}

    public DeviceConfig(String deviceId, long intervalMillis) {
        this.deviceId = deviceId;
        this.intervalMillis = intervalMillis;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }
}
