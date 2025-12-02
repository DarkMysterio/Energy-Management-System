package com.example.auth_service.dto;

import java.io.Serializable;

public class UserSyncMessage implements Serializable {
    private String operation; // CREATE, UPDATE, DELETE
    private String name;
    private Integer age;
    private String address;
    private String email;
    private String password;
    private String role;

    public UserSyncMessage() {}

    public UserSyncMessage(String operation, String name, Integer age, String address, String email, String password, String role) {
        this.operation = operation;
        this.name = name;
        this.age = age;
        this.address = address;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
