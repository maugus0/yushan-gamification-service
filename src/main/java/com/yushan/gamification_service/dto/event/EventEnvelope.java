package com.yushan.gamification_service.dto.event;

import com.fasterxml.jackson.databind.JsonNode;

public record EventEnvelope(
        String eventType,
        JsonNode payload
) {}
