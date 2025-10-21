package com.yushan.gamification_service.dto.event;

import java.util.UUID;

public record LevelUpEvent(
        UUID userId,

        int newLevel
) {}