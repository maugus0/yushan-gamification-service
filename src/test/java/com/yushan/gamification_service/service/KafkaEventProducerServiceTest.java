package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dto.event.UserActivityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaEventProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaEventProducerService kafkaEventProducerService;

    private UUID testUserId;
    private UserActivityEvent testEvent;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testEvent = new UserActivityEvent(
            testUserId,
            "gamification-service",
            "/api/gamification/test",
            "GET",
            LocalDateTime.now()
        );
    }

    @Test
    void publishUserActivityEvent_Success() {
        // Given
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq("active"), eq(testUserId.toString()), eq(testEvent)))
            .thenReturn(future);

        // When
        kafkaEventProducerService.publishUserActivityEvent(testEvent);

        // Then
        verify(kafkaTemplate).send("active", testUserId.toString(), testEvent);
    }

    @Test
    void publishUserActivityEvent_HandlesException() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Test exception"));

        // When & Then (should not throw exception)
        kafkaEventProducerService.publishUserActivityEvent(testEvent);
        verify(kafkaTemplate).send("active", testUserId.toString(), testEvent);
    }

    @Test
    void publishUserActivityEvent_CompletesSuccessfully() {
        // Given
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        kafkaEventProducerService.publishUserActivityEvent(testEvent);
        future.complete(mock(SendResult.class));

        // Then
        verify(kafkaTemplate).send("active", testUserId.toString(), testEvent);
    }
}
