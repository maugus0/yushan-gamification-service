package com.yushan.gamification_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.service.WelcomeRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);

    @Autowired
    private WelcomeRewardService welcomeRewardService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "user.events", groupId = "gamification-service")
    public void handleUserRegisteredEvent(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String userIdStr = rootNode.path("userId").asText(null);

            if (userIdStr == null) {
                log.warn("Received UserRegisteredEvent with null userId. Ignoring: {}", message);
                return;
            }

            UUID userId = UUID.fromString(userIdStr);
            log.info("Successfully parsed UserRegisteredEvent for userId: {}", userId);

            welcomeRewardService.grantNewUserReward(userId);

            log.info("Welcome reward processing initiated for userId: {}", userId);

        } catch (Exception e) {
            log.error("Failed to process UserRegisteredEvent: {}", message, e);
        }
    }
}