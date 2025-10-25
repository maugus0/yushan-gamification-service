package com.yushan.gamification_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLevelDTO {
    private int currentLevel;
    private double totalExp;
    private double expForNextLevel;
    private double expProgress;
    private OffsetDateTime lastLevelUp;
}
