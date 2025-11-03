package com.example.device_service.service;

import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.converter.DeviceConverter;
import com.example.device_service.entity.Device;
import com.example.device_service.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceConverter deviceConverter;

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
}
