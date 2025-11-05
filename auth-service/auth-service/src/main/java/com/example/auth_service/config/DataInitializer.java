package com.example.auth_service.config;

import com.example.auth_service.entity.AuthDetails;
import com.example.auth_service.repository.AuthRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if (authRepository.findByEmail("admin@yahoo.com").isEmpty()) {
                    AuthDetails admin = new AuthDetails();
                    admin.setEmail("admin@yahoo.com");
                    admin.setPassword(passwordEncoder.encode("admin"));
                    admin.setRole("ADMIN");
                    
                    authRepository.save(admin);
                    System.out.println("Default admin user created: admin@yahoo.com / admin");
                } else {
                    System.out.println("Admin user already exists");
                }
            }
        };
    }
}
