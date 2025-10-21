package com.yushan.gamification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.service.GamificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EngagementEventListener {

    @Autowired
    private GamificationService gamificationService;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Consume CommentCreatedEvent from engagement service
     */
    @KafkaListener(topics = "comment-events", groupId = "gamification-service")
    public void handleCommentCreatedEvent(@Payload String eventJson) {
        try {
            // Parse JSON to extract commentId and userId
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(eventJson);
            Integer commentId = jsonNode.get("commentId").asInt();
            String userIdStr = jsonNode.get("userId").asText();
            java.util.UUID userId = java.util.UUID.fromString(userIdStr);
            
            // Process comment reward
            gamificationService.processUserComment(userId, commentId.longValue());
        } catch (Exception e) {
            System.out.println("Error processing CommentCreatedEvent: " + e.getMessage());
        }
    }

    /**
     * Consume ReviewCreatedEvent from engagement service
     */
    @KafkaListener(topics = "review-events", groupId = "gamification-service")
    public void handleReviewCreatedEvent(@Payload String eventJson) {
        try {
            // Parse JSON to extract reviewId and userId
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(eventJson);
            Integer reviewId = jsonNode.get("reviewId").asInt();
            String userIdStr = jsonNode.get("userId").asText();
            java.util.UUID userId = java.util.UUID.fromString(userIdStr);
            
            // Process review reward
            gamificationService.processUserReview(userId, reviewId.longValue());
        } catch (Exception e) {
            System.out.println("Error processing ReviewCreatedEvent: " + e.getMessage());
        }
    }

    /**
     * Consume VoteCreatedEvent from engagement service
     */
    @KafkaListener(topics = "vote-events", groupId = "gamification-service")
    public void handleVoteCreatedEvent(@Payload String eventJson) {
        try {
            // Parse JSON to extract voteId and userId
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(eventJson);
            String userIdStr = jsonNode.get("userId").asText();
            java.util.UUID userId = java.util.UUID.fromString(userIdStr);
            
            // Process vote reward (EXP only, Yuan deduction is handled separately)
            gamificationService.processUserVote(userId);
        } catch (Exception e) {
            System.out.println("Error processing VoteCreatedEvent: " + e.getMessage());
        }
    }
}