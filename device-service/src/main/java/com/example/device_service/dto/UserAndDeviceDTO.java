package com.example.device_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserAndDeviceDTO {
    private UUID userID;
    private UUID deviceID;
}
