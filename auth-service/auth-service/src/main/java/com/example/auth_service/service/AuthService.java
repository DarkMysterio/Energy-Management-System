package com.example.auth_service.service;

import com.example.auth_service.config.RabbitMQConfig;
import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UserSyncMessage;
import com.example.auth_service.entity.AuthDetails;
import com.example.auth_service.repository.AuthRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private final AuthRepository authRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JWTService jwtService;
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JWTService jwtService, RabbitTemplate rabbitTemplate) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public AuthResponse register(RegisterRequest req) {
        authRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Email already registered");
        });

        AuthDetails user = new AuthDetails();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole().toUpperCase());

        authRepository.save(user);

        try {
            UserSyncMessage syncMessage = new UserSyncMessage(
                "CREATE",
                req.getName(),
                req.getAge(),
                req.getAddress(),
                req.getEmail(),
                user.getPassword(),
                user.getRole()
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_SYNC_QUEUE, syncMessage);
            System.out.println("User sync message sent to queue for: " + req.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send user sync message: " + e.getMessage());
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId().toString());
        return new AuthResponse(token, user.getRole(), user.getEmail());
    }
    
    public AuthResponse login(LoginRequest req) {
        AuthDetails user = authRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId().toString());
        return new AuthResponse(token, user.getRole(), user.getEmail());
    }
    
    public void deleteUserByEmail(String email) {
        AuthDetails user = authRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        authRepository.delete(user);

        try {
            UserSyncMessage syncMessage = new UserSyncMessage(
                "DELETE",
                null, null, null,
                email,
                null, null
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_SYNC_QUEUE, syncMessage);
            System.out.println("User delete sync message sent for: " + email);
        } catch (Exception e) {
            System.err.println("Failed to send user delete sync message: " + e.getMessage());
        }
    }
    
    public void deleteAllUsers() {
        java.util.List<AuthDetails> users = authRepository.findAll();
        
        authRepository.deleteAll();
        
        for (AuthDetails user : users) {
            try {
                UserSyncMessage syncMessage = new UserSyncMessage(
                    "DELETE",
                    null, null, null,
                    user.getEmail(),
                    null, null
                );
                rabbitTemplate.convertAndSend(RabbitMQConfig.USER_SYNC_QUEUE, syncMessage);
                System.out.println("User delete sync message sent for: " + user.getEmail());
            } catch (Exception e) {
                System.err.println("Failed to send user delete sync message for " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }
}
