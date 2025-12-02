package com.example.data_simulator.simulator;

import com.example.data_simulator.configuration.DeviceConfig;
import com.example.data_simulator.configuration.SimulatorProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DeviceDataSender {

    private final RabbitTemplate rabbitTemplate;
    private final SimulatorProperties properties;
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ScheduledExecutorService scheduler;

    public DeviceDataSender(RabbitTemplate rabbitTemplate, SimulatorProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        List<DeviceConfig> devices = loadDevicesFromJson();
        
        if (devices == null || devices.isEmpty()) {
            System.out.println("No devices found in devices.json!");
            return;
        }

        scheduler = Executors.newScheduledThreadPool(devices.size());
        
        for (DeviceConfig device : devices) {
            System.out.println("Scheduling device: " + device.getDeviceId() + " with interval: " + device.getIntervalMillis() + "ms");
            scheduler.scheduleAtFixedRate(
                () -> sendMeasurement(device.getDeviceId()),
                0,
                device.getIntervalMillis(),
                TimeUnit.MILLISECONDS
            );
        }
    }

    private List<DeviceConfig> loadDevicesFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource("devices.json");
            InputStream inputStream = resource.getInputStream();
            List<DeviceConfig> devices = objectMapper.readValue(inputStream, new TypeReference<List<DeviceConfig>>() {});
            System.out.println("Loaded " + devices.size() + " devices from devices.json");
            return devices;
        } catch (Exception e) {
            System.err.println("Could not load devices.json: " + e.getMessage());
            return null;
        }
    }

    private void sendMeasurement(String deviceId) {
        try {
            Instant now = Instant.now();
            double value = generateMeasurementValue(now);

            String json = String.format(
                    "{\"timestamp\":\"%s\",\"deviceId\":\"%s\",\"measurementValue\":%.3f}",
                    now.toString(), deviceId, value
            );

            rabbitTemplate.convertAndSend("", properties.getQueueName(), json);
            System.out.println("Sent measurement for device " + deviceId + ": " + value);
        } catch (Exception e) {
            System.err.println("Error sending measurement for device " + deviceId + ": " + e.getMessage());
        }
    }

    private double generateMeasurementValue(Instant timestamp) {
        LocalDateTime localTime = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        int hour = localTime.getHour();

        double base;
        if (hour < 6) {
            base = 0.1;
        } else if (hour < 12) {
            base = 0.25;
        } else if (hour < 18) {
            base = 0.4;
        } else {
            base = 0.7;
        }

        double noise = (random.nextDouble() - 0.5) * 0.1;
        double value = base + noise;

        return Math.max(0.01, value);
    }
}
