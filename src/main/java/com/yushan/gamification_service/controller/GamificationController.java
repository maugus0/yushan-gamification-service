package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.ApiResponse;
import com.yushan.gamification_service.dto.UserLoginRequestDTO;
import com.yushan.gamification_service.service.GamificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/gamification")
public class GamificationController {

    private final GamificationService gamificationService;

    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

//    @PostMapping("/events/login")
//    public ResponseEntity<ApiResponse<Void>> handleUserLogin(@RequestBody UserLoginRequestDTO requestDTO) {
//        UUID userId = requestDTO.getUserId();
//        gamificationService.processUserLogin(userId);
//        return ResponseEntity.ok(ApiResponse.success("Login event processed successfully for user " + userId));
//    }

}