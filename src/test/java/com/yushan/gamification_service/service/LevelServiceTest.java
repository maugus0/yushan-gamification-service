package com.yushan.gamification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LevelServiceTest {

    @InjectMocks
    private LevelService levelService;

    @ParameterizedTest
    @CsvSource({
        "-100, 1",
        "0, 1",
        "50, 1",
        "99.9, 1",
        "100, 2",
        "400, 2",
        "500, 3",
        "1500, 3",
        "2000, 4",
        "4000, 4",
        "5000, 5",
        "10000, 5"
    })
    void calculateLevel_ReturnsCorrectLevel(double exp, int expectedLevel) {
        // When
        int actualLevel = levelService.calculateLevel(exp);

        // Then
        assertEquals(expectedLevel, actualLevel);
    }

    @Test
    void calculateLevel_NullExp_ReturnsLevelOne() {
        // When
        int level = levelService.calculateLevel(null);

        // Then
        assertEquals(1, level);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 500.0",
        "2, 2000.0",
        "3, 5000.0"
    })
    void getExpForNextLevel_ReturnsCorrectThreshold(int currentLevel, Double expectedExp) {
        // When
        Double actualExp = levelService.getExpForNextLevel(currentLevel);

        // Then
        assertEquals(expectedExp, actualExp);
    }

    @Test
    void getExpForNextLevel_MaxLevel_ReturnsNull() {
        // Given
        int maxLevel = 5;

        // When
        Double expForNextLevel = levelService.getExpForNextLevel(maxLevel);

        // Then
        assertNull(expForNextLevel);
    }

    @Test
    void getExpForNextLevel_BeyondMaxLevel_ReturnsNull() {
        // Given
        int beyondMaxLevel = 6;

        // When
        Double expForNextLevel = levelService.getExpForNextLevel(beyondMaxLevel);

        // Then
        assertNull(expForNextLevel);
    }
}
