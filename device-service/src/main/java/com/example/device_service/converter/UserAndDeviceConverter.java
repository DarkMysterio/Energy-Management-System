package com.example.device_service.converter;

import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.entity.UserAndDevice;
import org.springframework.stereotype.Component;

@Component
public class UserAndDeviceConverter {

    public UserAndDevice convertToEntity(UserAndDeviceDTO dto){
        UserAndDevice userAndDevice = new UserAndDevice();
        userAndDevice.setDeviceID(dto.getDeviceID());
        userAndDevice.setUserID(dto.getUserID());

        return userAndDevice;
    }
    public UserAndDeviceDTO convertToDTO(UserAndDevice entity){
        UserAndDeviceDTO userAndDeviceDTO = new UserAndDeviceDTO();
        userAndDeviceDTO.setDeviceID(entity.getDeviceID());
        userAndDeviceDTO.setUserID(entity.getUserID());

        return userAndDeviceDTO;
    }
}
