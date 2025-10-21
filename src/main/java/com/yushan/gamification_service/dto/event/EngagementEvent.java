package com.yushan.gamification_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EngagementEvent(
        //"USER_COMMENT", "USER_REVIEW", "USER_VOTE"
        String eventType,

        UUID userId,

        //commentId, reviewId
        //(optional)
        Long entityId,

        OffsetDateTime timestamp
) {}