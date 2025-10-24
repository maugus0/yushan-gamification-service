package com.yushan.gamification_service.service;

import com.yushan.gamification_service.dao.*;
import com.yushan.gamification_service.entity.*;
import com.yushan.gamification_service.dto.event.LevelUpEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamificationServiceTest {

    @Mock
    private DailyRewardLogMapper dailyRewardLogMapper;

    @Mock
    private ExpTransactionMapper expTransactionMapper;

    @Mock
    private YuanTransactionMapper yuanTransactionMapper;

    @Mock
    private LevelService levelService;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserAchievementMapper userAchievementMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private GamificationService gamificationService;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        ReflectionTestUtils.setField(gamificationService, "dailyLoginExp", 5.0);
        ReflectionTestUtils.setField(gamificationService, "commentExp", 5.0);
        ReflectionTestUtils.setField(gamificationService, "reviewExp", 5.0);
        ReflectionTestUtils.setField(gamificationService, "voteExp", 3.0);
    }

    @Test
    void processUserLogin_FirstLoginOfDay_Success() {
        // Given
        when(dailyRewardLogMapper.findByUserId(testUserId))
            .thenReturn(Optional.empty());
        when(expTransactionMapper.sumAmountByUserId(testUserId))
            .thenReturn(0.0);
        when(levelService.calculateLevel(0.0))
            .thenReturn(1);

        // When
        gamificationService.processUserLogin(testUserId);

        // Then
        verify(expTransactionMapper).insert(any(ExpTransaction.class));
        verify(achievementService).checkAndUnlockLoginAchievements(testUserId);
        verify(dailyRewardLogMapper).insert(any(DailyRewardLog.class));
    }

    @Test
    void processUserLogin_AlreadyLoggedInToday_NoReward() {
        // Given
        DailyRewardLog existingLog = new DailyRewardLog();
        existingLog.setUserId(testUserId);
        existingLog.setLastRewardDate(LocalDate.now());

        when(dailyRewardLogMapper.findByUserId(testUserId))
            .thenReturn(Optional.of(existingLog));

        // When
        gamificationService.processUserLogin(testUserId);

        // Then
        verify(expTransactionMapper, never()).insert(any());
        verify(achievementService).checkAndUnlockLoginAchievements(testUserId);
    }
}
