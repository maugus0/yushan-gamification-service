package com.yushan.gamification_service.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


public class DailyRewardLog {

    private Long id;
    private UUID userId;
    private LocalDate lastRewardDate;
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDate getLastRewardDate() {
        return lastRewardDate;
    }

    public void setLastRewardDate(LocalDate lastRewardDate) {
        this.lastRewardDate = lastRewardDate;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}