package com.yushan.gamification_service.dto.achievement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AchievementDTO {
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private OffsetDateTime unlockedAt;
}
