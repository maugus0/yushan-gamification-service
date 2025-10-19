package com.yushan.gamification_service.service;

import org.springframework.stereotype.Service;

@Service
public class LevelService {
    private static final double[] LEVEL_THRESHOLDS = {100, 500, 2000, 5000};

    public int calculateLevel(Double exp) {
        if (exp == null || exp < 0) {
            return 1;
        }

        for (int i = 0; i < LEVEL_THRESHOLDS.length; i++) {
            if (exp < LEVEL_THRESHOLDS[i]) {
                return i + 1;
            }
        }

        return LEVEL_THRESHOLDS.length + 1;
    }

    public Double getExpForNextLevel(int currentLevel) {
        int nextLevelIndex = currentLevel;

        if (nextLevelIndex >= LEVEL_THRESHOLDS.length) {
            return null;
        }

       return LEVEL_THRESHOLDS[nextLevelIndex];
    }
}