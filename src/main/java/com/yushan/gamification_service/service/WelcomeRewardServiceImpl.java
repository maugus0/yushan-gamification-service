package com.yushan.gamification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WelcomeRewardServiceImpl implements WelcomeRewardService {

    @Autowired
    private GamificationService gamificationService;

    @Override
    public void grantNewUserReward(UUID userId) {
        gamificationService.processUserLogin(userId);
    }
}