package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.achievement.AchievementDTO;
import com.yushan.gamification_service.dto.common.ApiResponse;
import com.yushan.gamification_service.dto.stats.GamificationStatsDTO;
import com.yushan.gamification_service.dto.transaction.YuanTransactionDTO;
import com.yushan.gamification_service.service.GamificationService;
import com.yushan.gamification_service.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Gamification Stats", description = "APIs for viewing gamification statistics and achievements")
@RestController
@RequestMapping("/api/v1/gamification")
@CrossOrigin(origins = "*")
public class GamificationStatsController {

    private final GamificationService gamificationService;

    public GamificationStatsController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @Operation(summary = "[USER] Get my gamification stats", description = "Get current user's gamification statistics including level, EXP, and Yuan balance")
    @GetMapping("/stats/me")
    public ApiResponse<GamificationStatsDTO> getMyGamificationStats() {
        UUID userId = SecurityUtils.getCurrentUserId();

        GamificationStatsDTO stats = gamificationService.getGamificationStatsForUser(userId);

        return ApiResponse.success(stats);
    }

    @Operation(summary = "[USER] Get my Yuan transactions", description = "Get current user's Yuan transaction history with pagination")
    @GetMapping("/yuan/transactions/me")
    public ApiResponse<List<YuanTransactionDTO>> getMyYuanTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<YuanTransactionDTO> history = gamificationService.getTransactionHistory(userId, page, size);
        return ApiResponse.success(history);
    }

    @Operation(summary = "[USER] Get my achievements", description = "Get current user's unlocked achievements")
    @GetMapping("/achievements/me")
    public ApiResponse<List<AchievementDTO>> getMyUnlockedAchievements() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<AchievementDTO> achievements = gamificationService.getUnlockedAchievements(userId);
        return ApiResponse.success(achievements);
    }
}