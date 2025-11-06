package com.example.device_service.controller;

import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.service.UserAndDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/devices/assign")
public class UserAndDeviceController {

    @Autowired
    private UserAndDeviceService userAndDeviceService;

    @PostMapping()
    public ResponseEntity<Void> assignDeviceToUser(@RequestBody UserAndDeviceDTO dto){
        userAndDeviceService.assignDeviceToUser(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping()
    public ResponseEntity<List<UserAndDeviceDTO>> getAllAssignedDevices(){
        return ResponseEntity.ok(userAndDeviceService.getAllAssignedDevices());
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteAllAssignedDevicesBelongingtoUser(@PathVariable UUID id){
        userAndDeviceService.deleteAllAssignedDevicesBelongingtoUser(id);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @DeleteMapping("/device/{id}")
    public ResponseEntity<Void> deleteAllAssignedDevicesBelongingtoDevice(@PathVariable UUID id){
        userAndDeviceService.deleteAllAssignedDevicesBelongingtoDevice(id);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @DeleteMapping("/assignment/{userId}/{deviceId}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable UUID userId,
            @PathVariable UUID deviceId) {
        userAndDeviceService.deleteAssignment(userId, deviceId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteAllAssignedDevices(){
        userAndDeviceService.deleteAllAssignedDevices();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
