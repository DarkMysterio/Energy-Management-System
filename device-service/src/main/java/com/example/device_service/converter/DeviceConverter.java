package com.example.device_service.converter;

import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class DeviceConverter {

    public Device deviceDTOtoDevice(DeviceDTO dto){
        Device device = new Device();
        device.setId(dto.getId());
        device.setName(dto.getName());
        device.setConsumption(dto.getConsumption());

        return  device;
    }

    public DeviceDTO deviceToDeviceDTO(Device device){
        DeviceDTO dto = new DeviceDTO();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setConsumption(device.getConsumption());

        return dto;
    }
}
