package com.example.device_service.repository;

import com.example.device_service.entity.UserAndDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAndDeviceRepository extends JpaRepository<UserAndDevice, UUID> {

    boolean existsByUserIDAndDeviceID(UUID userID, UUID deviceID);

    boolean existsByDeviceID(UUID deviceID);

    List<UserAndDevice> findByUserID(UUID userID);

    List<UserAndDevice> findByDeviceID(UUID deviceID);

    void deleteByUserIDAndDeviceID(UUID userID, UUID deviceID);
}
