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

    @KafkaListener(topics = "comment-events", groupId = "gamification-service")
    public void handleCommentCreatedEvent(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String eventType = rootNode.path("eventType").asText();
            String userIdStr = rootNode.path("userId").asText(null);

            if (!"COMMENT_CREATED".equals(eventType) || userIdStr == null) {
                log.warn("Received invalid COMMENT_CREATED event. Ignoring: {}", message);
                return;
            }

            UUID userId = UUID.fromString(userIdStr);
            Integer commentId = rootNode.path("commentId").asInt(0);
            
            if (commentId <= 0) {
                log.warn("COMMENT_CREATED event missing valid commentId: {}", message);
                return;
            }

            log.info("Received COMMENT_CREATED event for user: {}, commentId: {}", userId, commentId);
            gamificationService.processUserComment(userId, commentId.longValue());
        } catch (Exception e) {
            log.error("Failed to process COMMENT_CREATED event: {}", message, e);
        }
    }

    @KafkaListener(topics = "review-events", groupId = "gamification-service")
    public void handleReviewCreatedEvent(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String eventType = rootNode.path("eventType").asText();
            String userIdStr = rootNode.path("userId").asText(null);

            if (!"REVIEW_CREATED".equals(eventType) || userIdStr == null) {
                log.warn("Received invalid REVIEW_CREATED event. Ignoring: {}", message);
                return;
            }

            UUID userId = UUID.fromString(userIdStr);
            Integer reviewId = rootNode.path("reviewId").asInt(0);
            
            if (reviewId <= 0) {
                log.warn("REVIEW_CREATED event missing valid reviewId: {}", message);
                return;
            }

            log.info("Received REVIEW_CREATED event for user: {}, reviewId: {}", userId, reviewId);
            gamificationService.processUserReview(userId, reviewId.longValue());
        } catch (Exception e) {
            log.error("Failed to process REVIEW_CREATED event: {}", message, e);
        }
    }

    @KafkaListener(topics = "vote-events", groupId = "gamification-service")
    public void handleVoteCreatedEvent(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String eventType = rootNode.path("eventType").asText();
            String userIdStr = rootNode.path("userId").asText(null);

            if (!"VOTE_CREATED".equals(eventType) || userIdStr == null) {
                log.warn("Received invalid VOTE_CREATED event. Ignoring: {}", message);
                return;
            }

            UUID userId = UUID.fromString(userIdStr);
            log.info("Received VOTE_CREATED event for user: {}", userId);
            gamificationService.processUserVote(userId);
        } catch (Exception e) {
            log.error("Failed to process VOTE_CREATED event: {}", message, e);
        }
    }
}