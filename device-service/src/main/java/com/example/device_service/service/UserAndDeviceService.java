package com.example.device_service.service;

import com.example.device_service.converter.UserAndDeviceConverter;
import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.dto.UserCacheDTO;
import com.example.device_service.entity.UserAndDevice;
import com.example.device_service.repository.UserAndDeviceRepository;
import com.example.device_service.repository.UserCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAndDeviceService{
    @Autowired
    private UserAndDeviceRepository userAndDeviceRepository;

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Autowired
    private UserAndDeviceConverter converter;


    public void assignDeviceToUser(UserAndDeviceDTO dto){

        boolean existsInUserRepo = userCacheRepository.existsByUserId(dto.getUserID());
        if(!existsInUserRepo){
            return;
        }
        boolean exists = userAndDeviceRepository.existsByUserIDAndDeviceID(dto.getUserID(), dto.getDeviceID());
        if(exists){
            return;
        }


        UserAndDevice userAndDevice = converter.convertToEntity(dto);
        userAndDeviceRepository.save(userAndDevice);
    }

    public List<UserAndDeviceDTO> getAllAssignedDevices(){
        List<UserAndDevice> list = userAndDeviceRepository.findAll();
        List<UserAndDeviceDTO> listReturned = new ArrayList<>();
        for(UserAndDevice userAndDevice : list){
            listReturned.add(converter.convertToDTO(userAndDevice));
        }
        return listReturned;
    }

    public void deleteAllAssignedDevices(){
        userAndDeviceRepository.deleteAll();
    }


}
