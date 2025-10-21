package com.yushan.gamification_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.service.GamificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EngagementEventListener {

    private static final Logger log = LoggerFactory.getLogger(EngagementEventListener.class);

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = {"comment-events", "review-events", "vote-events"}, groupId = "gamification-service")
    public void handleEngagementEvents(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String eventType = rootNode.path("eventType").asText();
            String userIdStr = rootNode.path("userId").asText(null);

            if (userIdStr == null || eventType == null || eventType.isEmpty()) {
                log.warn("Received an engagement event with missing userId or eventType. Ignoring: {}", message);
                return;
            }

            UUID userId = UUID.fromString(userIdStr);
            log.info("Received engagement event: {} for user: {}", eventType, userId);


            switch (eventType) {
                case "COMMENT_CREATED":
                    long commentId = rootNode.path("commentId").asLong(0);
                    if (commentId > 0) {
                        gamificationService.processUserComment(userId, commentId);
                    } else {
                        log.warn("COMMENT_CREATED event missing valid commentId: {}", message);
                    }
                    break;

                case "REVIEW_CREATED":
                    long reviewId = rootNode.path("reviewId").asLong(0);
                    if (reviewId > 0) {
                        gamificationService.processUserReview(userId, reviewId);
                    } else {
                        log.warn("REVIEW_CREATED event missing valid reviewId: {}", message);
                    }
                    break;

                case "VOTE_CREATED":
                    gamificationService.processUserVote(userId);
                    break;

                default:
                    log.warn("Unknown engagement event type received: {}", eventType);
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to process engagement event: {}", message, e);
        }
    }
}