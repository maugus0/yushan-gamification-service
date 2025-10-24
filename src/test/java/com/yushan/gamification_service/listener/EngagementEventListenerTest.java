package com.yushan.gamification_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.service.GamificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngagementEventListenerTest {

    @Mock
    private GamificationService gamificationService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonNode jsonNode;

    @InjectMocks
    private EngagementEventListener engagementEventListener;

    private final UUID testUserId = UUID.randomUUID();
    private final String testUserIdStr = testUserId.toString();

    @BeforeEach
    void setUp() throws JsonProcessingException {
        // Common setup to mock objectMapper and JsonNode behavior
        when(objectMapper.readTree(any(String.class))).thenReturn(jsonNode);
        JsonNode userIdNode = mock(JsonNode.class);
        when(jsonNode.get("userId")).thenReturn(userIdNode);
        when(userIdNode.asText()).thenReturn(testUserIdStr);
    }

    @Test
    void handleCommentCreatedEvent_shouldProcessComment() throws Exception {
        // Given
        long commentId = 123L;
        String eventJson = String.format("{\"commentId\":%d,\"userId\":\"%s\"}", commentId, testUserIdStr);

        JsonNode commentIdNode = mock(JsonNode.class);
        when(jsonNode.get("commentId")).thenReturn(commentIdNode);
        when(commentIdNode.asInt()).thenReturn((int) commentId);

        doNothing().when(gamificationService).processUserComment(testUserId, commentId);

        // When
        engagementEventListener.handleCommentCreatedEvent(eventJson);

        // Then
        verify(objectMapper).readTree(eventJson);
        verify(gamificationService).processUserComment(testUserId, commentId);
    }

    @Test
    void handleReviewCreatedEvent_shouldProcessReview() throws Exception {
        // Given
        long reviewId = 456L;
        String eventJson = String.format("{\"reviewId\":%d,\"userId\":\"%s\"}", reviewId, testUserIdStr);

        JsonNode reviewIdNode = mock(JsonNode.class);
        when(jsonNode.get("reviewId")).thenReturn(reviewIdNode);
        when(reviewIdNode.asInt()).thenReturn((int) reviewId);

        doNothing().when(gamificationService).processUserReview(testUserId, reviewId);

        // When
        engagementEventListener.handleReviewCreatedEvent(eventJson);

        // Then
        verify(objectMapper).readTree(eventJson);
        verify(gamificationService).processUserReview(testUserId, reviewId);
    }

    @Test
    void handleVoteCreatedEvent_shouldProcessVote() throws Exception {
        // Given
        String eventJson = String.format("{\"userId\":\"%s\"}", testUserIdStr);
        doNothing().when(gamificationService).processUserVote(testUserId);

        // When
        engagementEventListener.handleVoteCreatedEvent(eventJson);

        // Then
        verify(objectMapper).readTree(eventJson);
        verify(gamificationService).processUserVote(testUserId);
    }

    @Test
    void handleEvent_shouldCatchJsonProcessingException() throws Exception {
        // Given
        String invalidJson = "invalid-json";

        // When
        engagementEventListener.handleCommentCreatedEvent(invalidJson);

        // Then
        // Verify that the service method was not called due to the exception
        verify(gamificationService, never()).processUserComment(any(), anyLong());
    }
}
