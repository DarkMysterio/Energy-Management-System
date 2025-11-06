package com.example.device_service.controller;


import com.example.device_service.dto.UserCacheDTO;
import com.example.device_service.service.UserAndDeviceService;
import com.example.device_service.service.UserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserCacheController {

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private UserAndDeviceService userAndDeviceService;

    @PostMapping
    public ResponseEntity<UUID> cacheUserOnCreate(@RequestBody UserCacheDTO userCacheDTO){
        UUID id = userCacheService.cacheUserOnCreate(userCacheDTO);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Boolean> isUserCached(@PathVariable UUID id){
        boolean exists = userCacheService.isUserCached(id);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserCache(@PathVariable UUID userId) {
        // First delete all device assignments for this user
        userAndDeviceService.deleteAllAssignedDevicesBelongingtoUser(userId);
        
        // Then delete the user from cache
        userCacheService.deleteUserCache(userId);

        return ResponseEntity.noContent().build();
    }

}
