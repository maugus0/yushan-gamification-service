package com.yushan.gamification_service.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamificationStatsDTO {

    private int level;
    private Double currentExp;
    private Double totalExpForNextLevel;
    private Double yuanBalance;
}
