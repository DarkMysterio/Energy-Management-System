package com.example.monitoring_service.dto;

import java.util.List;

public class DailyConsumptionResponse {
    private String deviceId;
    private String date;
    private List<HourlyData> hourlyData;
    private double totalDayKwh;

    public DailyConsumptionResponse() {}

    public DailyConsumptionResponse(String deviceId, String date, List<HourlyData> hourlyData, double totalDayKwh) {
        this.deviceId = deviceId;
        this.date = date;
        this.hourlyData = hourlyData;
        this.totalDayKwh = totalDayKwh;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<HourlyData> getHourlyData() { return hourlyData; }
    public void setHourlyData(List<HourlyData> hourlyData) { this.hourlyData = hourlyData; }

    public double getTotalDayKwh() { return totalDayKwh; }
    public void setTotalDayKwh(double totalDayKwh) { this.totalDayKwh = totalDayKwh; }

    public static class HourlyData {
        private int hour;
        private double kwh;

        public HourlyData() {}

        public HourlyData(int hour, double kwh) {
            this.hour = hour;
            this.kwh = kwh;
        }

        public int getHour() { return hour; }
        public void setHour(int hour) { this.hour = hour; }

        public double getKwh() { return kwh; }
        public void setKwh(double kwh) { this.kwh = kwh; }
    }
}
