package com.example.device_service.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class UserCacheDTO {

    private UUID userId;
    private String name;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserCacheDTO() {
    }

    public UserCacheDTO(UUID userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}
