package com.example.monitoring_service.dto;

import java.util.List;

public class UserTotalConsumptionResponse {
    
    private String date;
    private List<DeviceConsumption> deviceConsumptions;
    private double totalKwh;

    public UserTotalConsumptionResponse() {}

    public UserTotalConsumptionResponse(String date, List<DeviceConsumption> deviceConsumptions, double totalKwh) {
        this.date = date;
        this.deviceConsumptions = deviceConsumptions;
        this.totalKwh = totalKwh;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<DeviceConsumption> getDeviceConsumptions() { return deviceConsumptions; }
    public void setDeviceConsumptions(List<DeviceConsumption> deviceConsumptions) { this.deviceConsumptions = deviceConsumptions; }

    public double getTotalKwh() { return totalKwh; }
    public void setTotalKwh(double totalKwh) { this.totalKwh = totalKwh; }

    public static class DeviceConsumption {
        private String deviceId;
        private String deviceName;
        private double kwh;

        public DeviceConsumption() {}

        public DeviceConsumption(String deviceId, String deviceName, double kwh) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.kwh = kwh;
        }

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

        public double getKwh() { return kwh; }
        public void setKwh(double kwh) { this.kwh = kwh; }
    }
}
