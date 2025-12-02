package com.example.monitoring_service.dto;

import java.util.List;

public class MultiDeviceRequest {
    private List<DeviceInfo> devices;

    public MultiDeviceRequest() {}

    public List<DeviceInfo> getDevices() { return devices; }
    public void setDevices(List<DeviceInfo> devices) { this.devices = devices; }

    public static class DeviceInfo {
        private String deviceId;
        private String name;

        public DeviceInfo() {}

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
