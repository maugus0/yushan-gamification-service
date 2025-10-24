package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.common.ApiResponse;
import com.yushan.gamification_service.dto.user.UserLevelDTO;
import com.yushan.gamification_service.dto.vote.VoteCheckResponseDTO;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamificationControllerTest {

    @Mock
    private GamificationService gamificationService;

    @InjectMocks
    private GamificationController gamificationController;

    private UUID testUserId;
    private UserLevelDTO testUserLevelDTO;
    private VoteCheckResponseDTO testVoteCheckDTO;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserLevelDTO = new UserLevelDTO(1, 50.0, 100.0, 0.5, OffsetDateTime.now());
        testVoteCheckDTO = new VoteCheckResponseDTO(true, 10.0, 5.0, "Eligible to vote");
    }

    @Test
    void getUserLevel_Success() {
        // Given
        when(gamificationService.getUserLevel(testUserId)).thenReturn(testUserLevelDTO);

        // When
        ApiResponse<UserLevelDTO> response = gamificationController.getUserLevel(testUserId);

        // Then
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(testUserLevelDTO, response.getData());
    }

    @Test
    void rewardComment_Success() {
        // Given
        Long commentId = 1L;
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

            // When
           ApiResponse<String> response = gamificationController.rewardComment(commentId);

            // Then
            assertEquals(200, response.getCode());
            verify(gamificationService).rewardComment(testUserId, commentId);
        }
    }

    @Test
    void rewardReview_Success() {
        // Given
        Long reviewId = 1L;
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

            // When
            ApiResponse<String> response = gamificationController.rewardReview(reviewId);

            // Then
            assertEquals(200, response.getCode());
            verify(gamificationService).rewardReview(testUserId, reviewId);
        }
    }

    @Test
    void rewardVote_Success() {
        // Given
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

            // When
            ApiResponse<String> response = gamificationController.rewardVote();

            // Then
            assertEquals(200, response.getCode());
            verify(gamificationService).rewardVote(testUserId);
        }
    }

    @Test
    void checkVoteEligibility_Success() {
        // Given
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
            when(gamificationService.checkVoteEligibility(testUserId)).thenReturn(testVoteCheckDTO);

            // When
            ApiResponse<VoteCheckResponseDTO> response = gamificationController.checkVoteEligibility();

            // Then
            assertEquals(200, response.getCode());
            assertNotNull(response.getData());
            assertEquals(testVoteCheckDTO, response.getData());
        }
    }

    @Test
    void updateYuanAfterVote_Success() {
        // Given
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

            // When
           ApiResponse<String> response = gamificationController.updateYuanAfterVote();

            // Then
            assertEquals(200, response.getCode());
            verify(gamificationService).updateYuanAfterVote(testUserId);
        }
    }
}
