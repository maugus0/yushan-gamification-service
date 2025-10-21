package com.yushan.gamification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.dto.event.LevelUpEvent;
import com.yushan.gamification_service.service.AchievementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InternalEventListener {

    private static final Logger log = LoggerFactory.getLogger(InternalEventListener.class);

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "internal_gamification_events", groupId = "gamification-service-internal")
    public void handleLevelUpEvent(String message) {
        try {
            LevelUpEvent event = objectMapper.readValue(message, LevelUpEvent.class);
            log.info("Received internal LevelUpEvent for user {}, new level: {}", event.userId(), event.newLevel());

            achievementService.checkAndUnlockLevelAchievements(event.userId(), event.newLevel());

        } catch (Exception e) {
            log.error("Failed to process internal LevelUpEvent: {}", message, e);
        }
    }
}
