package com.example.device_service.controller;


import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Device", description = "Device management API")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Operation(summary = "Get all devices")
    @ApiResponse(responseCode = "200", description = "List of devices")
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices(){
        return ResponseEntity.ok(deviceService.findAllDevices());
    }

    @Operation(summary = "Create device")
    @ApiResponse(responseCode = "201", description = "Device created")
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
    @Operation(summary = "Update device")
    @ApiResponse(responseCode = "204", description = "Device updated")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDevice(@PathVariable UUID id,@RequestBody DeviceDTO dto){
        deviceService.updateDevice(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(summary = "Delete all devices")
    @ApiResponse(responseCode = "204", description = "Devices deleted")
    @DeleteMapping
    public ResponseEntity<Void> deleteDevices(){
        deviceService.deleteAllDevices();
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(summary = "Update device name")
    @ApiResponse(responseCode = "204", description = "Name updated")
    @PatchMapping("/name/{id}")
    public ResponseEntity<Void> partialUpdateName(@PathVariable UUID id, @RequestBody DeviceDTO dto){
        deviceService.partialUpdateName(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(summary = "Update device consumption")
    @ApiResponse(responseCode = "204", description = "Consumption updated")
    @PatchMapping("/consumption/{id}")
    public ResponseEntity<Void> partialUpdateConsumption(@PathVariable UUID id, @RequestBody DeviceDTO dto){
        deviceService.partialUpdateConsumption(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

}
