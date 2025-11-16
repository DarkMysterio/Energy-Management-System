package com.example.device_service.service;

import com.example.device_service.converter.UserAndDeviceConverter;
import com.example.device_service.dto.UserAndDeviceDTO;
import com.example.device_service.entity.UserAndDevice;
import com.example.device_service.repository.UserAndDeviceRepository;
import com.example.device_service.repository.UserCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserAndDeviceService{
    @Autowired
    private UserAndDeviceRepository userAndDeviceRepository;

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Autowired
    private UserAndDeviceConverter converter;

    @Transactional
    public void assignDeviceToUser(UserAndDeviceDTO dto){

        boolean existsInUserRepo = userCacheRepository.existsByUserId(dto.getUserID());
        if(!existsInUserRepo){
            return;
        }
        boolean existsDeviceInRepo =  userAndDeviceRepository.existsByDeviceID(dto.getDeviceID());
        if(existsDeviceInRepo){
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

    @Transactional
    public void deleteAllAssignedDevicesBelongingtoUser(UUID userId) {
        List<UserAndDevice> assignments = userAndDeviceRepository.findByUserID(userId);
        userAndDeviceRepository.deleteAll(assignments);
    }

    @Transactional
    public void deleteAllAssignedDevicesBelongingtoDevice(UUID deviceId) {
        List<UserAndDevice> assignments = userAndDeviceRepository.findByDeviceID(deviceId);
        userAndDeviceRepository.deleteAll(assignments);
    }

    @Transactional
    public void deleteAssignment(UUID userId, UUID deviceId) {
        userAndDeviceRepository.deleteByUserIDAndDeviceID(userId, deviceId);
    }
}
