package com.example.device_service.service;

import com.example.device_service.config.RabbitMQConfig;
import com.example.device_service.dto.UserCacheDTO;
import com.example.device_service.dto.UserToDeviceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserSyncConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncConsumer.class);

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private UserAndDeviceService userAndDeviceService;

    @RabbitListener(queues = RabbitMQConfig.USER_TO_DEVICE_QUEUE)
    public void handleUserSync(UserToDeviceMessage message) {
        LOGGER.info("Received user sync message: operation={}, userId={}", message.getOperation(), message.getUserId());

        try {
            switch (message.getOperation()) {
                case "CREATE":
                    createUserCache(message);
                    break;
                case "DELETE":
                    deleteUserCache(message.getUserId());
                    break;
                default:
                    LOGGER.warn("Unknown operation: {}", message.getOperation());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing user sync message: {}", e.getMessage(), e);
        }
    }

    private void createUserCache(UserToDeviceMessage message) {
        try {
            UUID userId = UUID.fromString(message.getUserId());

            if (userCacheService.isUserCached(userId)) {
                LOGGER.info("User already cached: {}", userId);
                return;
            }

            UserCacheDTO dto = new UserCacheDTO(userId, message.getName());
            userCacheService.cacheUserOnCreate(dto);
            LOGGER.info("User cached from sync message: {}", userId);
        } catch (Exception e) {
            LOGGER.error("Failed to cache user: {}", e.getMessage(), e);
        }
    }

    private void deleteUserCache(String userIdStr) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            userAndDeviceService.deleteAllAssignedDevicesBelongingtoUser(userId);
            userCacheService.deleteUserCache(userId);
            LOGGER.info("User cache deleted: {}", userId);
        } catch (Exception e) {
            LOGGER.error("Failed to delete user cache: {}", e.getMessage(), e);
        }
    }
}
