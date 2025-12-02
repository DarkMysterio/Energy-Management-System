package com.example.monitoring_service.service;

import com.example.monitoring_service.config.RabbitConfig;
import com.example.monitoring_service.dto.DeviceSyncMessage;
import com.example.monitoring_service.entity.Device;
import com.example.monitoring_service.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceSyncConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSyncConsumer.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @RabbitListener(queues = RabbitConfig.DEVICE_SYNC_QUEUE)
    public void handleDeviceSync(DeviceSyncMessage message) {
        LOGGER.info("Received device sync message: operation={}, deviceId={}", message.getOperation(), message.getDeviceId());

        try {
            switch (message.getOperation()) {
                case "CREATE":
                    createDevice(message);
                    break;
                case "UPDATE":
                    updateDevice(message);
                    break;
                case "DELETE":
                    deleteDevice(message.getDeviceId());
                    break;
                default:
                    LOGGER.warn("Unknown operation: {}", message.getOperation());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing device sync message: {}", e.getMessage(), e);
        }
    }

    private void createDevice(DeviceSyncMessage message) {
        try {
            UUID deviceId = UUID.fromString(message.getDeviceId());

            if (deviceRepository.existsById(deviceId)) {
                LOGGER.info("Device already exists: {}", deviceId);
                return;
            }

            Device device = new Device(deviceId, message.getName(), message.getConsumption());
            deviceRepository.save(device);
            LOGGER.info("Device created from sync message: {}", deviceId);
        } catch (Exception e) {
            LOGGER.error("Failed to create device: {}", e.getMessage(), e);
        }
    }

    private void updateDevice(DeviceSyncMessage message) {
        try {
            UUID deviceId = UUID.fromString(message.getDeviceId());
            Optional<Device> existing = deviceRepository.findById(deviceId);
            
            if (existing.isPresent()) {
                Device device = existing.get();
                if (message.getName() != null) {
                    device.setName(message.getName());
                }
                if (message.getConsumption() != null) {
                    device.setMaxConsumption(message.getConsumption());
                }
                deviceRepository.save(device);
                LOGGER.info("Device updated from sync message: {}", deviceId);
            } else {
                createDevice(message);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update device: {}", e.getMessage(), e);
        }
    }

    private void deleteDevice(String deviceIdStr) {
        try {
            UUID deviceId = UUID.fromString(deviceIdStr);
            if (deviceRepository.existsById(deviceId)) {
                deviceRepository.deleteById(deviceId);
                LOGGER.info("Device deleted from sync message: {}", deviceId);
            } else {
                LOGGER.warn("Device not found for deletion: {}", deviceId);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to delete device: {}", e.getMessage(), e);
        }
    }
}
