package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.common.ApiResponse;
import com.yushan.gamification_service.dto.user.UserLevelDTO;
import com.yushan.gamification_service.dto.vote.VoteCheckResponseDTO;
import com.yushan.gamification_service.service.GamificationService;
import com.yushan.gamification_service.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Gamification Management", description = "APIs for managing gamification features")
@RestController
@RequestMapping("/api/v1/gamification")
@CrossOrigin(origins = "*")
public class GamificationController {

    private final GamificationService gamificationService;

    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    // User Level API
    @Operation(summary = "[USER] Get user level", description = "Get user's current level, EXP, and progress information")
    @GetMapping("/users/{userId}/level")
    public ResponseEntity<ApiResponse<UserLevelDTO>> getUserLevel(@PathVariable UUID userId) {
        UserLevelDTO userLevel = gamificationService.getUserLevel(userId);
        return ResponseEntity.ok(ApiResponse.success("User level retrieved successfully", userLevel));
    }

    // Comment APIs
    @Operation(summary = "[USER] Reward comment", description = "Award EXP to user for creating a comment")
    @PostMapping("/comments/{commentId}/reward")
    public ResponseEntity<ApiResponse<String>> rewardComment(@PathVariable Long commentId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        gamificationService.rewardComment(userId, commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment rewarded successfully"));
    }

    // Review APIs
    @Operation(summary = "[USER] Reward review", description = "Award EXP to user for creating a review")
    @PostMapping("/reviews/{reviewId}/reward")
    public ResponseEntity<ApiResponse<String>> rewardReview(@PathVariable Long reviewId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        gamificationService.rewardReview(userId, reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review rewarded successfully"));
    }

    // Vote APIs
    @Operation(summary = "[USER] Process vote reward", description = "Award 3 EXP to user for voting")
    @PostMapping("/votes/reward")
    public ResponseEntity<ApiResponse<String>> rewardVote() {
        UUID userId = SecurityUtils.getCurrentUserId();
        gamificationService.rewardVote(userId);
        return ResponseEntity.ok(ApiResponse.success("Vote processed successfully"));
    }

    // Vote check API
    @Operation(summary = "[USER] Check vote eligibility", description = "Check if user has enough Yuan to vote")
    @GetMapping("/votes/check")
    public ResponseEntity<ApiResponse<VoteCheckResponseDTO>> checkVoteEligibility() {
        UUID userId = SecurityUtils.getCurrentUserId();
        VoteCheckResponseDTO response = gamificationService.checkVoteEligibility(userId);
        return ResponseEntity.ok(ApiResponse.success("Vote eligibility checked", response));
    }

    // Yuan update after vote API
    @Operation(summary = "[USER] Deduct Yuan for vote", description = "Deduct 1 Yuan from user balance for voting")
    @PostMapping("/votes/deduct-yuan")
    public ResponseEntity<ApiResponse<String>> updateYuanAfterVote() {
        UUID userId = SecurityUtils.getCurrentUserId();
        gamificationService.updateYuanAfterVote(userId);
        return ResponseEntity.ok(ApiResponse.success("Yuan deducted for vote"));
    }
}
