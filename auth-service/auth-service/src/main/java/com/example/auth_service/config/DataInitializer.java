package com.example.auth_service.config;

import com.example.auth_service.dto.UserSyncMessage;
import com.example.auth_service.entity.AuthDetails;
import com.example.auth_service.repository.AuthRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
                    
                    try {
                        UserSyncMessage syncMessage = new UserSyncMessage(
                            "CREATE",
                            "Administrator",
                            30,
                            "System",
                            "admin@yahoo.com",
                            hashedPassword,
                            "ADMIN"
                        );
                        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_SYNC_QUEUE, syncMessage);
                        System.out.println("Admin user sync message sent to queue");
                    } catch (Exception e) {
                        System.err.println("Warning: Could not send admin sync message to queue: " + e.getMessage());
                    }

                } else {
                    System.out.println("Admin user already exists");
                }
            }
        };
    }
}
