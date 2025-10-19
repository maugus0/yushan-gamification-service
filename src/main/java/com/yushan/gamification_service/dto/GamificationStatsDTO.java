package com.yushan.gamification_service.dto;

public class GamificationStatsDTO {

    private int level;
    private Double currentExp;
    private Double totalExpForNextLevel;
    private Double yuanBalance;


    public GamificationStatsDTO(int level, Double currentExp, Double totalExpForNextLevel, Double yuanBalance) {
        this.level = level;
        this.currentExp = currentExp;
        this.totalExpForNextLevel = totalExpForNextLevel;
        this.yuanBalance = yuanBalance;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Double getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(Double currentExp) {
        this.currentExp = currentExp;
    }

    public Double getTotalExpForNextLevel() {
        return totalExpForNextLevel;
    }

    public void setTotalExpForNextLevel(Double totalExpForNextLevel) {
        this.totalExpForNextLevel = totalExpForNextLevel;
    }

    public Double getYuanBalance() {
        return yuanBalance;
    }

    public void setYuanBalance(Double yuanBalance) {
        this.yuanBalance = yuanBalance;
    }
}