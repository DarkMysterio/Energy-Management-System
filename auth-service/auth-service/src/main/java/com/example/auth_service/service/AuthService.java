package com.example.auth_service.service;

import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UserServiceDTO;
import com.example.auth_service.entity.AuthDetails;
import com.example.auth_service.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {
    @Autowired
    private final AuthRepository authRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JWTService jwtService;
    @Autowired
    private final RestTemplate restTemplate;
    
    @Value("${USER_SERVICE_URL:http://spring-demo:8080}")
    private String userServiceUrl;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JWTService jwtService, RestTemplate restTemplate) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.restTemplate = restTemplate;
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
            UserServiceDTO userDto = new UserServiceDTO(
                req.getName(),
                req.getAge(),
                req.getAddress(),
                req.getEmail(),
                user.getPassword() // sent hashed pasword
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserServiceDTO> request = new HttpEntity<>(userDto, headers);
            
            restTemplate.postForEntity(userServiceUrl + "/people", request, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to create user in user-service: " + e.getMessage());
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getRole(), user.getEmail());
    }
    
    public AuthResponse login(LoginRequest req) {
        AuthDetails user = authRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getRole(), user.getEmail());
    }
    
    public void deleteUserByEmail(String email) {
        AuthDetails user = authRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        authRepository.delete(user);
    }
}
