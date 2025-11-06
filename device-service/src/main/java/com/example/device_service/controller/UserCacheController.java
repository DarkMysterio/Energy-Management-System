package com.example.device_service.controller;


import com.example.device_service.dto.UserCacheDTO;
import com.example.device_service.service.UserAndDeviceService;
import com.example.device_service.service.UserCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@Tag(name = "User Cache", description = "API for managing user cache and synchronization")
public class UserCacheController {

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private UserAndDeviceService userAndDeviceService;

    @Operation(
            summary = "Cache user on creation",
            description = "Stores user information in cache when a new user is created"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully cached, returns the user ID"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<UUID> cacheUserOnCreate(@RequestBody UserCacheDTO userCacheDTO){
        UUID id = userCacheService.cacheUserOnCreate(userCacheDTO);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    
    @Operation(
            summary = "Check if user is cached",
            description = "Verifies whether a user with the specified ID exists in the cache"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns true if user exists in cache, false otherwise"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Boolean> isUserCached(@PathVariable UUID id){
        boolean exists = userCacheService.isUserCached(id);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
    
    @Operation(
            summary = "Delete user from cache",
            description = "Removes the user from cache and deletes all associated device assignments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully deleted from cache and all assignments removed"),
            @ApiResponse(responseCode = "404", description = "User not found in cache"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserCache(@PathVariable UUID userId) {
        userAndDeviceService.deleteAllAssignedDevicesBelongingtoUser(userId);
        userCacheService.deleteUserCache(userId);

        return ResponseEntity.noContent().build();
    }

}
