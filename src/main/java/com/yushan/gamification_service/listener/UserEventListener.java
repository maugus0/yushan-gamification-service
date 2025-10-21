package com.yushan.gamification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.dto.event.UserRegisteredEvent;
import com.yushan.gamification_service.service.WelcomeRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);

            log.info("Successfully parsed UserRegisteredEvent for userId: {}", event.userId());

            welcomeRewardService.grantNewUserReward(event.userId());

            log.info("Welcome reward processing initiated for userId: {}", event.userId());

        } catch (Exception e) {
            log.error("Failed to process UserRegisteredEvent: {}", message, e);
        }
    }
}