package com.example.device_service.dto;

import java.io.Serializable;

public class UserToDeviceMessage implements Serializable {
    private String operation; // CREATE, DELETE
    private String userId;
    private String name;

    public UserToDeviceMessage() {}

    public UserToDeviceMessage(String operation, String userId, String name) {
        this.operation = operation;
        this.userId = userId;
        this.name = name;
    }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
