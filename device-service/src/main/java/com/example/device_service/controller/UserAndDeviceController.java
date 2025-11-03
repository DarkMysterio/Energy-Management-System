package com.example.device_service.controller;

import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.service.UserAndDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAssignedDevices(){
        userAndDeviceService.deleteAllAssignedDevices();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
