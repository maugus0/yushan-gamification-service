package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dto.achievement.AchievementDTO;
import com.yushan.gamification_service.dto.stats.GamificationStatsDTO;
import com.yushan.gamification_service.dto.transaction.YuanTransactionDTO;
import com.yushan.gamification_service.dto.common.PageResponseDTO;
import com.yushan.gamification_service.dto.transaction.AdminYuanTransactionDTO;
import java.time.OffsetDateTime;

import java.util.UUID;

import java.util.List;

public interface GamificationService {

    void processUserLogin(UUID userId);

    void processUserComment(UUID userId, Long commentId);

    void processUserReview(UUID userId, Long reviewId);

    GamificationStatsDTO getGamificationStatsForUser(UUID userId);

    List<YuanTransactionDTO> getTransactionHistory(UUID userId, int page, int size);

    List<AchievementDTO> getUnlockedAchievements(UUID userId);

    void processUserVote(UUID userId);

    PageResponseDTO<AdminYuanTransactionDTO> findYuanTransactionsForAdmin(
            UUID userId,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            int page,
            int size
    );
}