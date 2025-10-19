package com.yushan.gamification_service.dto;

import java.util.UUID;

public class UserLoginRequestDTO {

    private UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}