package com.example.device_service.controller;


import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices(){
        return ResponseEntity.ok(deviceService.findAllDevices());
    }

    @PostMapping
    public ResponseEntity<Void> createDevice(@RequestBody DeviceDTO dto){
        UUID id = deviceService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDevice(@PathVariable UUID id,@RequestBody DeviceDTO dto){
        deviceService.updateDevice(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteDevices(){
        deviceService.deleteAllDevices();
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @PatchMapping("/name/{id}")
    public ResponseEntity<Void> partialUpdateName(@PathVariable UUID id, @RequestBody DeviceDTO dto){
        deviceService.partialUpdateName(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @PatchMapping("/consumption/{id}")
    public ResponseEntity<Void> partialUpdateConsumption(@PathVariable UUID id, @RequestBody DeviceDTO dto){
        deviceService.partialUpdateConsumption(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

}
