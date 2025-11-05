package com.example.auth_service.config;

import com.example.auth_service.entity.AuthDetails;
import com.example.auth_service.repository.AuthRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DataInitializer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${USER_SERVICE_URL:http://spring-demo:8080}")
    private String userServiceUrl;

    @Bean
    CommandLineRunner initDatabase(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if (authRepository.findByEmail("admin@yahoo.com").isEmpty()) {
                    String hashedPassword = passwordEncoder.encode("admin");
                    
                    AuthDetails admin = new AuthDetails();
                    admin.setEmail("admin@yahoo.com");
                    admin.setPassword(hashedPassword);
                    admin.setRole("ADMIN");
                    
                    authRepository.save(admin);
                    System.out.println("Default admin user created: admin@yahoo.com / admin");
                    
                    Map<String, Object> userProfile = new HashMap<>();
                    userProfile.put("email", "admin@yahoo.com");
                    userProfile.put("name", "Administrator");
                    userProfile.put("password", hashedPassword);
                    userProfile.put("age", 30);
                    userProfile.put("address", "System");
                    
                    String userServiceCreateUrl = userServiceUrl + "/people";
                        
                    restTemplate.postForObject(userServiceCreateUrl, userProfile, Object.class);
                    

                } else {
                    System.out.println("Admin user already exists");
                }
            }
        };
    }
}
