package com.example.device_service.repository;

import com.example.device_service.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCacheRepository extends JpaRepository<UserCache, UUID> {
    boolean existsByUserId(UUID userId);
}
