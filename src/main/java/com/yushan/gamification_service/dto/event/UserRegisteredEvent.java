package com.yushan.gamification_service.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String username,
        String email,
        LocalDateTime registeredAt
) {}