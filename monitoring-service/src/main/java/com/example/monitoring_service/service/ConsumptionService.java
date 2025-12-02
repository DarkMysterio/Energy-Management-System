package com.example.monitoring_service.service;

import com.example.monitoring_service.dto.DailyConsumptionResponse;
import com.example.monitoring_service.dto.DailyConsumptionResponse.HourlyData;
import com.example.monitoring_service.dto.MultiDeviceRequest;
import com.example.monitoring_service.dto.UserTotalConsumptionResponse;
import com.example.monitoring_service.dto.UserTotalConsumptionResponse.DeviceConsumption;
import com.example.monitoring_service.entity.HourlyConsumption;
import com.example.monitoring_service.repository.DeviceRepository;
import com.example.monitoring_service.repository.HourlyConsumptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConsumptionService {

    private final HourlyConsumptionRepository hourlyConsumptionRepository;
    private final DeviceRepository deviceRepository;

    public ConsumptionService(HourlyConsumptionRepository hourlyConsumptionRepository, DeviceRepository deviceRepository) {
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
        this.deviceRepository = deviceRepository;
    }

    public boolean deviceExists(UUID deviceId) {
        return deviceRepository.existsById(deviceId);
    }

    public DailyConsumptionResponse getDailyConsumption(UUID deviceId, LocalDate date) {

        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        Instant startInstant = startOfDay.toInstant();
        Instant endInstant = endOfDay.toInstant();


        List<HourlyConsumption> consumptions = hourlyConsumptionRepository
                .findAllByDeviceIdAndHourStartBetween(deviceId, startInstant, endInstant);


        Map<Integer, Double> hourMap = consumptions.stream()
                .collect(Collectors.toMap(
                        hc -> ZonedDateTime.ofInstant(hc.getHourStart(), ZoneId.of("UTC")).getHour(),
                        HourlyConsumption::getTotalKwh,
                        (a, b) -> a + b
                ));


        List<HourlyData> hourlyData = new ArrayList<>();
        double totalKwh = 0.0;

        for (int hour = 0; hour < 24; hour++) {
            double kwh = hourMap.getOrDefault(hour, 0.0);
            hourlyData.add(new HourlyData(hour, kwh));
            totalKwh += kwh;
        }

        return new DailyConsumptionResponse(
                deviceId.toString(),
                date.toString(),
                hourlyData,
                totalKwh
        );
    }

    public UserTotalConsumptionResponse getTotalConsumptionForDevices(List<MultiDeviceRequest.DeviceInfo> devices, LocalDate date) {
        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        Instant startInstant = startOfDay.toInstant();
        Instant endInstant = endOfDay.toInstant();

        List<DeviceConsumption> deviceConsumptions = new ArrayList<>();
        double grandTotal = 0.0;

        for (MultiDeviceRequest.DeviceInfo deviceInfo : devices) {
            try {
                UUID deviceId = UUID.fromString(deviceInfo.getDeviceId());
                
                List<HourlyConsumption> consumptions = hourlyConsumptionRepository
                        .findAllByDeviceIdAndHourStartBetween(deviceId, startInstant, endInstant);

                double deviceTotal = consumptions.stream()
                        .mapToDouble(HourlyConsumption::getTotalKwh)
                        .sum();

                deviceConsumptions.add(new DeviceConsumption(
                        deviceInfo.getDeviceId(),
                        deviceInfo.getName() != null ? deviceInfo.getName() : "Unknown",
                        deviceTotal
                ));

                grandTotal += deviceTotal;
            } catch (Exception e) {

            }
        }

        return new UserTotalConsumptionResponse(date.toString(), deviceConsumptions, grandTotal);
    }
}
