package com.yushan.gamification_service.service;

import java.util.UUID;

public interface AchievementService {

    void checkAndUnlockLoginAchievements(UUID userId);

    void checkAndUnlockCommentAchievements(UUID userId, long totalCommentCount);

    void checkAndUnlockReviewAchievements(UUID userId, long totalReviewCount);

    void checkAndUnlockVoteAchievements(UUID userId, long totalVoteCount);

    void checkAndUnlockLevelAchievements(UUID userId, int newLevel);
}