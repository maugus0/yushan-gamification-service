package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.ApiResponse;
import com.yushan.gamification_service.dto.UserLoginRequestDTO;
import com.yushan.gamification_service.service.GamificationService;
import org.springframework.http.ResponseEntity;
import com.yushan.gamification_service.util.TestTokenGenerator;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/gamification")
public class GamificationController {

    private final GamificationService gamificationService;
    private final TestTokenGenerator testTokenGenerator;

    public GamificationController(GamificationService gamificationService, TestTokenGenerator testTokenGenerator) {
        this.gamificationService = gamificationService;
        this.testTokenGenerator = testTokenGenerator;
    }

    @PostMapping("/events/login")
    public ResponseEntity<ApiResponse<Void>> handleUserLogin(@RequestBody UserLoginRequestDTO requestDTO) {
        UUID userId = requestDTO.getUserId();
        gamificationService.processUserLogin(userId);
        return ResponseEntity.ok(ApiResponse.success("Login event processed successfully for user " + userId));
    }

}