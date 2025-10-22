package com.yushan.gamification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.dto.event.EventEnvelope;
import com.yushan.gamification_service.dto.event.UserLoggedInEvent;
import com.yushan.gamification_service.dto.event.UserRegisteredEvent;
import com.yushan.gamification_service.service.GamificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventListener {

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "user.events", groupId = "gamification-service")
    public void handleUserEvent(String message) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);

            switch (envelope.eventType()) {
                case "UserRegisteredEvent":
                    UserRegisteredEvent registeredEvent = objectMapper.treeToValue(envelope.payload(), UserRegisteredEvent.class);
                    log.info("Processing UserRegisteredEvent for email: {}", registeredEvent.email());
                    gamificationService.processUserRegistration(registeredEvent.uuid());
                    break;

                case "UserLoggedInEvent":
                    UserLoggedInEvent loggedInEvent = objectMapper.treeToValue(envelope.payload(), UserLoggedInEvent.class);
                    log.info("Processing UserLoggedInEvent for email: {}", loggedInEvent.email());
                    gamificationService.processUserLogin(loggedInEvent.uuid());
                    break;

                default:
                    log.warn("Received unknown event type: {}", envelope.eventType());
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to process event message: {}", message, e);
        }
    }
}