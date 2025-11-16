package com.example.device_service.controller;


import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(
            summary = "Get all devices",
            description = "Retrieves a complete list of all devices in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of devices"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<List<DeviceDTO>> getAllDevices(){
        return ResponseEntity.ok(deviceService.findAllDevices());
    }

    @Operation(
            summary = "Create device",
            description = "Creates a new device in the system and returns the location URI"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid device data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> createDevice(@RequestBody DeviceDTO dto){
        UUID id = deviceService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }
    @Operation(
            summary = "Update device",
            description = "Updates all properties of an existing device identified by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid device data provided"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateDevice(@PathVariable UUID id,@RequestBody DeviceDTO dto){
        deviceService.updateDevice(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(
            summary = "Delete all devices",
            description = "Removes all devices from the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All devices successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteDevices(){
        deviceService.deleteAllDevices();
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(
            summary = "Update device name",
            description = "Partially updates only the name property of a device"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device name successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid name provided"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @PatchMapping("/name/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> partialUpdateName(@PathVariable UUID id, @RequestBody DeviceDTO dto){
        deviceService.partialUpdateName(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(
            summary = "Update device consumption",
            description = "Partially updates only the consumption/energy usage property of a device"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device consumption successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid consumption value provided"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @PatchMapping("/consumption/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> partialUpdateConsumption(@PathVariable UUID id, @RequestBody DeviceDTO dto){
        deviceService.partialUpdateConsumption(id,dto);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(
            summary = "Delete device by ID",
            description = "Removes a specific device identified by its unique device ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteDevicesById(@PathVariable UUID id){
        deviceService.deleteDevicesById(id);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

}
