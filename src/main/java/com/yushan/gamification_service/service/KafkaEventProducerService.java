package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dto.event.UserActivityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka event producer service for publishing gamification events
 */
@Slf4j
@Service
public class KafkaEventProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Generic method to publish events to Kafka
     */
    private void publishEvent(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Successfully sent event to topic: {}, partition: {}, offset: {}", 
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send event to topic: {}", topic, ex);
            }
        });
    }

    /**
     * Publish user activity event
     */
    public void publishUserActivityEvent(UserActivityEvent event) {
        try {
            publishEvent("active", event.userId().toString(), event);
            log.info("Published user activity event for user: {}, service: {}, endpoint: {}", 
                     event.userId(), event.serviceName(), event.endpoint());
        } catch (Exception e) {
            log.error("Failed to publish user activity event for user: {}", event.userId(), e);
        }
    }
}
