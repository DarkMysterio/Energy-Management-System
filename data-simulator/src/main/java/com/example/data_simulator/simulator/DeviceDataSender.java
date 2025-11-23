package com.example.data_simulator.simulator;

import com.example.data_simulator.configuration.SimulatorProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

@Component
public class DeviceDataSender {

    private final RabbitTemplate rabbitTemplate;
    private final SimulatorProperties properties;
    private final Random random = new Random();

    public DeviceDataSender(RabbitTemplate rabbitTemplate, SimulatorProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }


    @Scheduled(fixedRateString = "${simulator.interval-millis:600000}")
    public void sendMeasurement() {
        Instant now = Instant.now();
        double value = generateMeasurementValue(now);

        String json = String.format(
                "{\"timestamp\":\"%s\",\"deviceId\":\"%s\",\"measurementValue\":%.4f}",
                now.toString(), properties.getDeviceId(), value
        );

        rabbitTemplate.convertAndSend("", properties.getQueueName(), json);
    }

    /**
     * Simple daily pattern:
     *  - 00-06: low
     *  - 06-12: medium-low
     *  - 12-18: medium
     *  - 18-24: high
     */
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
