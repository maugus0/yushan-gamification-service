package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.achievement.AchievementDTO;
import com.yushan.gamification_service.dto.common.ApiResponse;
import com.yushan.gamification_service.dto.stats.GamificationStatsDTO;
import com.yushan.gamification_service.dto.transaction.YuanTransactionDTO;
import com.yushan.gamification_service.service.GamificationService;
import com.yushan.gamification_service.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamificationStatsControllerTest {

    @Mock
    private GamificationService gamificationService;

    @InjectMocks
    private GamificationStatsController gamificationStatsController;

    private UUID testUserId;
    private GamificationStatsDTO testStatsDTO;
    private List<YuanTransactionDTO> testTransactions;
    private List<AchievementDTO> testAchievements;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testStatsDTO = new GamificationStatsDTO(
            1, // level
            50.0, // currentExp
            100.0, // nextLevelExp
            10.0 // yuan
        );

        testTransactions = Arrays.asList(
            new YuanTransactionDTO(5.0, "Daily Login Reward", OffsetDateTime.now()),
            new YuanTransactionDTO(-1.0, "Vote Cost", OffsetDateTime.now())
        );

        testAchievements = Arrays.asList(
            new AchievementDTO("WELCOME_TO_YUSHAN", "Welcome to Yushan", "First login", "", OffsetDateTime.now()),
            new AchievementDTO("FIRST_CRY", "First Cry", "First comment", "", OffsetDateTime.now())
        );
    }

    @Test
    void getMyGamificationStats_Success() {
        // Given
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
            when(gamificationService.getGamificationStatsForUser(testUserId)).thenReturn(testStatsDTO);

            // When
            ApiResponse<GamificationStatsDTO> response = gamificationStatsController.getMyGamificationStats();

            // Then
            assertEquals(200, response.getCode());
            assertEquals(testStatsDTO, response.getData());
            verify(gamificationService).getGamificationStatsForUser(testUserId);
        }
    }

    @Test
    void getMyYuanTransactions_Success() {
        // Given
        int page = 0;
        int size = 20;
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
            when(gamificationService.getTransactionHistory(testUserId, page, size)).thenReturn(testTransactions);

            // When
            ApiResponse<List<YuanTransactionDTO>> response =
                gamificationStatsController.getMyYuanTransactions(page, size);

            // Then
            assertEquals(200, response.getCode());
            assertEquals(testTransactions, response.getData());
            verify(gamificationService).getTransactionHistory(testUserId, page, size);
        }
    }

    @Test
    void getMyUnlockedAchievements_Success() {
        // Given
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
            when(gamificationService.getUnlockedAchievements(testUserId)).thenReturn(testAchievements);

            // When
            ApiResponse<List<AchievementDTO>> response =
                gamificationStatsController.getMyUnlockedAchievements();

            // Then
            assertEquals(200, response.getCode());
            assertEquals(testAchievements, response.getData());
            verify(gamificationService).getUnlockedAchievements(testUserId);
        }
    }
}
