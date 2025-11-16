package com.example.device_service.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_and_device__device_id", columnNames = "device_id")
        }
)
public class UserAndDevice {
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "userId", nullable = false)
    private UUID userID;

    @Column(name = "device_id", nullable = false, unique = true)
    private UUID deviceID;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public UUID getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(UUID deviceID) {
        this.deviceID = deviceID;
    }
}
