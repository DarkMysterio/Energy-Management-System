package com.example.device_service.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeviceDTO {

    private UUID id;
    private String name;
    private Double consumption;


}
