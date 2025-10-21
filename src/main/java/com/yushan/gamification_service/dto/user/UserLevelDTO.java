package com.yushan.gamification_service.dto.user;

import java.time.OffsetDateTime;

public class UserLevelDTO {
    private int currentLevel;
    private double totalExp;
    private double expForNextLevel;
    private double expProgress;
    private OffsetDateTime lastLevelUp;

    public UserLevelDTO() {}

    public UserLevelDTO(int currentLevel, double totalExp, double expForNextLevel, double expProgress, OffsetDateTime lastLevelUp) {
        this.currentLevel = currentLevel;
        this.totalExp = totalExp;
        this.expForNextLevel = expForNextLevel;
        this.expProgress = expProgress;
        this.lastLevelUp = lastLevelUp;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public double getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(double totalExp) {
        this.totalExp = totalExp;
    }

    public double getExpForNextLevel() {
        return expForNextLevel;
    }

    public void setExpForNextLevel(double expForNextLevel) {
        this.expForNextLevel = expForNextLevel;
    }

    public double getExpProgress() {
        return expProgress;
    }

    public void setExpProgress(double expProgress) {
        this.expProgress = expProgress;
    }

    public OffsetDateTime getLastLevelUp() {
        return lastLevelUp;
    }

    public void setLastLevelUp(OffsetDateTime lastLevelUp) {
        this.lastLevelUp = lastLevelUp;
    }
}
