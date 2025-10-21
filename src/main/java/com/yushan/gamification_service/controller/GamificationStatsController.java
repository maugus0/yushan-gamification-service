package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.achievement.AchievementDTO;
import com.yushan.gamification_service.dto.common.ApiResponse;
import com.yushan.gamification_service.dto.stats.GamificationStatsDTO;
import com.yushan.gamification_service.dto.transaction.YuanTransactionDTO;
import com.yushan.gamification_service.service.GamificationService;
import com.yushan.gamification_service.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
public class GamificationStatsController {

    private final GamificationService gamificationService;

    public GamificationStatsController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @GetMapping("/stats/me")
    public ResponseEntity<ApiResponse<GamificationStatsDTO>> getMyGamificationStats() {
        UUID userId = SecurityUtils.getCurrentUserId();

        GamificationStatsDTO stats = gamificationService.getGamificationStatsForUser(userId);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/yuan/transactions/me")
    public ResponseEntity<ApiResponse<List<YuanTransactionDTO>>> getMyYuanTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<YuanTransactionDTO> history = gamificationService.getTransactionHistory(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/achievements/me")
    public ResponseEntity<ApiResponse<List<AchievementDTO>>> getMyUnlockedAchievements() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<AchievementDTO> achievements = gamificationService.getUnlockedAchievements(userId);
        return ResponseEntity.ok(ApiResponse.success(achievements));
    }
}