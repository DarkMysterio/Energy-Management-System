package com.example.device_service.service;

import com.example.device_service.converter.UserCacheConverter;
import com.example.device_service.dto.UserCacheDTO;
import com.example.device_service.entity.UserCache;
import com.example.device_service.repository.UserCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserCacheService {

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Autowired
    private UserCacheConverter userCacheConverter;

    public UUID cacheUserOnCreate(UserCacheDTO userCacheDTO){
        UserCache userCache = this.userCacheConverter.convertToEntity(userCacheDTO);
        userCache = this.userCacheRepository.save(userCache);
        return userCache.getUserId();
    }
    public void deleteUserCache(UUID uuid){
        this.userCacheRepository.deleteById(uuid);
    }
    public boolean isUserCached(UUID uuid){
        return this.userCacheRepository.existsByUserId(uuid);
    }

    public void deleteById(UUID userId) {
        userCacheRepository.deleteById(userId);
    }
}
