package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dto.AchievementDTO;
import com.yushan.gamification_service.dto.GamificationStatsDTO;
import com.yushan.gamification_service.dto.YuanTransactionDTO;

import java.util.UUID;

import java.util.List;

public interface GamificationService {

    void processUserLogin(UUID userId);

    void processUserComment(UUID userId, Long commentId);

    void processUserReview(UUID userId, Long reviewId);

    GamificationStatsDTO getGamificationStatsForUser(UUID userId);

    List<YuanTransactionDTO> getTransactionHistory(UUID userId, int page, int size);

    List<AchievementDTO> getUnlockedAchievements(UUID userId);
}