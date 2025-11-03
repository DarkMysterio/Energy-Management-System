package com.example.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceDTO {
    private String name;
    private Integer age;
    private String address;
    private String email;
    private String password;
}
