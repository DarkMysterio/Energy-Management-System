package com.example.device_service.converter;

import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.dto.UserCacheDTO;
import com.example.device_service.entity.UserAndDevice;
import com.example.device_service.entity.UserCache;
import org.springframework.stereotype.Component;

@Component
public class UserCacheConverter {

    public UserCache convertToEntity(UserCacheDTO dto){
        UserCache userCache = new UserCache();
        userCache.setUserId(dto.getUserId());
        userCache.setName(dto.getName());

        return userCache;
    }
    public UserCacheDTO convertToDTO(UserCache entity){
        UserCacheDTO userCacheDTO = new UserCacheDTO();
        userCacheDTO.setUserId(entity.getUserId());
        userCacheDTO.setName(entity.getName());

        return userCacheDTO;
    }
}
