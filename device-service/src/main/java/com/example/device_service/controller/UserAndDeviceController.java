package com.example.device_service.controller;

import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.service.UserAndDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/devices/assign")
@Tag(name = "User-Device Assignment", description = "API for managing device assignments to users")
public class UserAndDeviceController {

    @Autowired
    private UserAndDeviceService userAndDeviceService;

    @Operation(
            summary = "Assign device to user",
            description = "Creates a new assignment between a device and a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device successfully assigned to user"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User or device not found")
    })
    @PostMapping()
    public ResponseEntity<Void> assignDeviceToUser(@RequestBody UserAndDeviceDTO dto){
        userAndDeviceService.assignDeviceToUser(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Get all device assignments",
            description = "Retrieves a list of all device-to-user assignments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of assignments"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping()
    public ResponseEntity<List<UserAndDeviceDTO>> getAllAssignedDevices(){
        return ResponseEntity.ok(userAndDeviceService.getAllAssignedDevices());
    }

    @Operation(
            summary = "Delete all device assignments for a user",
            description = "Removes all device assignments associated with the specified user ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All user device assignments successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteAllAssignedDevicesBelongingtoUser(@PathVariable UUID id){
        userAndDeviceService.deleteAllAssignedDevicesBelongingtoUser(id);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(
            summary = "Delete all user assignments for a device",
            description = "Removes all user assignments associated with the specified device ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All device user assignments successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @DeleteMapping("/device/{id}")
    public ResponseEntity<Void> deleteAllAssignedDevicesBelongingtoDevice(@PathVariable UUID id){
        userAndDeviceService.deleteAllAssignedDevicesBelongingtoDevice(id);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @Operation(
            summary = "Delete specific device assignment",
            description = "Removes the assignment between a specific user and device"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Assignment successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @DeleteMapping("/assignment/{userId}/{deviceId}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable UUID userId,
            @PathVariable UUID deviceId) {
        userAndDeviceService.deleteAssignment(userId, deviceId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Delete all device assignments",
            description = "Removes all device-to-user assignments from the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All assignments successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllAssignedDevices(){
        userAndDeviceService.deleteAllAssignedDevices();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
