package com.example.device_service.service;

import com.example.device_service.config.RabbitMQConfig;
import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.dto.DeviceSyncMessage;
import com.example.device_service.converter.DeviceConverter;
import com.example.device_service.entity.Device;
import com.example.device_service.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserAndDeviceService userAndDeviceService;

    @Autowired
    private DeviceConverter deviceConverter;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<DeviceDTO> findAllDevices(){
        List<Device> list = deviceRepository.findAll();
        List<DeviceDTO> listDTO = new ArrayList<>();
        for(Device device : list){
            listDTO.add(deviceConverter.deviceToDeviceDTO(device));
        }
        return  listDTO;
    }

    public UUID create(DeviceDTO dto){
        Device device = deviceConverter.deviceDTOtoDevice(dto);
        device = deviceRepository.save(device);
        
        // Send device sync message to RabbitMQ
        try {
            DeviceSyncMessage syncMessage = new DeviceSyncMessage(
                "CREATE",
                device.getId().toString(),
                device.getName(),
                device.getConsumption()
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_SYNC_QUEUE, syncMessage);
            LOGGER.info("Device sync message sent for: {}", device.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to send device sync message: {}", e.getMessage());
        }
        
        return device.getId();
    }

    public void deleteAllDevices(){
        deviceRepository.deleteAll();
    }

    public void updateDevice(UUID id, DeviceDTO dto){
        Optional<Device> device = deviceRepository.findById(id);
        if(!device.isPresent()){
            return;
        }
        Device deviceEntity = device.get();
        if(dto.getName() != null){
            deviceEntity.setName(dto.getName());
        }
        if(dto.getConsumption() != null){
            deviceEntity.setConsumption(dto.getConsumption());
        }
        deviceRepository.save(deviceEntity);
        
        // Send update sync message
        try {
            DeviceSyncMessage syncMessage = new DeviceSyncMessage(
                "UPDATE",
                deviceEntity.getId().toString(),
                deviceEntity.getName(),
                deviceEntity.getConsumption()
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_SYNC_QUEUE, syncMessage);
            LOGGER.info("Device update sync message sent for: {}", deviceEntity.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to send device update sync message: {}", e.getMessage());
        }
    }

    public void partialUpdateConsumption(UUID id,DeviceDTO dto){
        Optional<Device> device = deviceRepository.findById(id);
        if(!device.isPresent()){
            return;
        }
        Device deviceEntity = device.get();
        deviceEntity.setConsumption(dto.getConsumption());
        deviceRepository.save(deviceEntity);
    }
    public void partialUpdateName(UUID id,DeviceDTO dto){
        Optional<Device> device = deviceRepository.findById(id);
        if(!device.isPresent()){
            return;
        }
        Device deviceEntity = device.get();
        deviceEntity.setName(dto.getName());
        deviceRepository.save(deviceEntity);
    }

    public void deleteDevicesById(UUID id) {
        userAndDeviceService.deleteAllAssignedDevicesBelongingtoDevice(id);
        deviceRepository.deleteById(id);
        
        // Send delete sync message
        try {
            DeviceSyncMessage syncMessage = new DeviceSyncMessage(
                "DELETE",
                id.toString(),
                null,
                null
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_SYNC_QUEUE, syncMessage);
            LOGGER.info("Device delete sync message sent for: {}", id);
        } catch (Exception e) {
            LOGGER.error("Failed to send device delete sync message: {}", e.getMessage());
        }
    }
}
