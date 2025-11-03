package com.example.device_service.controller;


import com.example.device_service.dto.UserCacheDTO;
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

    @PostMapping
    public ResponseEntity<UUID> cacheUserOnCreate(@RequestBody UserCacheDTO userCacheDTO){
        UUID id = userCacheService.cacheUserOnCreate(userCacheDTO);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCacheOnDelete(@PathVariable UUID id){
       userCacheService.deleteUserCache(id);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Boolean> isUserCached(@PathVariable UUID id){
        boolean exists = userCacheService.isUserCached(id);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

}
