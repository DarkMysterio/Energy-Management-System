package com.example.device_service.repository;

import com.example.device_service.entity.UserAndDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserAndDeviceRepository extends JpaRepository<UserAndDevice, UUID> {

    boolean existsByUserIDAndDeviceID(UUID userID, UUID deviceID);
}
