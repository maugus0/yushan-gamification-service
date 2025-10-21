package com.yushan.gamification_service.service;

import java.util.UUID;

public interface WelcomeRewardService {

    void grantNewUserReward(UUID userId);
}