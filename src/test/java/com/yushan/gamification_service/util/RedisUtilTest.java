package com.yushan.gamification_service.util;

import com.yushan.gamification_service.dto.stats.GamificationStatsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisUtilTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisUtil redisUtil;

    private final String testKey = "test:key";
    private final String testValue = "test-value";
    private final String testUserId = UUID.randomUUID().toString();

    @Test
    void set_withDuration_shouldCallValueOpsSet() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Duration ttl = Duration.ofMinutes(10);
        redisUtil.set(testKey, testValue, ttl);
        verify(valueOperations).set(testKey, testValue, ttl);
    }

    @Test
    void set_withSeconds_shouldCallValueOpsSet() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        long ttlSeconds = 600;
        redisUtil.set(testKey, testValue, ttlSeconds);
        verify(valueOperations).set(testKey, testValue, ttlSeconds, TimeUnit.SECONDS);
    }

    @Test
    void get_shouldReturnValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(testKey)).thenReturn(testValue);
        Object result = redisUtil.get(testKey);
        assertEquals(testValue, result);
        verify(valueOperations).get(testKey);
    }

    @Test
    void get_withType_shouldReturnTypedValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(testKey)).thenReturn(testValue);
        String result = redisUtil.get(testKey, String.class);
        assertEquals(testValue, result);
    }

    @Test
    void get_withWrongType_shouldReturnNull() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(testKey)).thenReturn(123); // Return an Integer
        String result = redisUtil.get(testKey, String.class); // Expect a String
        assertNull(result);
    }

    @Test
    void delete_singleKey_shouldCallTemplateDelete() {
        redisUtil.delete(testKey);
        verify(redisTemplate).delete(testKey);
    }

    @Test
    void delete_multipleKeys_shouldCallTemplateDelete() {
        Set<String> keys = Collections.singleton(testKey);
        redisUtil.delete(keys);
        verify(redisTemplate).delete(keys);
    }

    @Test
    void exists_shouldCallHasKey() {
        when(redisTemplate.hasKey(testKey)).thenReturn(true);
        assertTrue(redisUtil.exists(testKey));
        verify(redisTemplate).hasKey(testKey);
    }

    @Test
    void increment_shouldCallValueOpsIncrement() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(testKey)).thenReturn(1L);
        Long result = redisUtil.increment(testKey);
        assertEquals(1L, result);
        verify(valueOperations).increment(testKey);
    }

    @Test
    void increment_withDelta_shouldCallValueOpsIncrement() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        long delta = 5L;
        when(valueOperations.increment(testKey, delta)).thenReturn(5L);
        Long result = redisUtil.increment(testKey, delta);
        assertEquals(5L, result);
        verify(valueOperations).increment(testKey, delta);
    }

    @Test
    void expire_shouldCallTemplateExpire() {
        Duration ttl = Duration.ofHours(1);
        redisUtil.expire(testKey, ttl);
        verify(redisTemplate).expire(testKey, ttl);
    }

    @Test
    void keys_shouldCallTemplateKeys() {
        String pattern = "test:*";
        Set<String> expectedKeys = Collections.singleton("test:key1");
        when(redisTemplate.keys(pattern)).thenReturn(expectedKeys);
        Set<String> result = redisUtil.keys(pattern);
        assertEquals(expectedKeys, result);
        verify(redisTemplate).keys(pattern);
    }

    @Test
    void cacheAchievement_shouldSetWithCorrectKeyAndTTL() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Integer achievementId = 1;
        redisUtil.cacheAchievement(achievementId, testValue);
        verify(valueOperations).set("achievement:" + achievementId, testValue, Duration.ofHours(2));
    }

    @Test
    void getCachedAchievement_shouldGetWithCorrectKey() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Integer achievementId = 1;
        redisUtil.getCachedAchievement(achievementId);
        verify(valueOperations).get("achievement:" + achievementId);
    }

    @Test
    void cacheGamificationStats_shouldSetWithCorrectKeyAndTTL() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        GamificationStatsDTO stats = new GamificationStatsDTO();
        redisUtil.cacheGamificationStats(testUserId, stats);
        verify(valueOperations).set("stats:user:" + testUserId, stats, Duration.ofMinutes(30));
    }

    @Test
    void getCachedGamificationStats_shouldGetWithCorrectKey() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisUtil.getCachedGamificationStats(testUserId, GamificationStatsDTO.class);
        verify(valueOperations).get("stats:user:" + testUserId);
    }

    @Test
    void invalidateUserCaches_shouldDeleteUserAndStatsKeys() {
        String userKey = "user:profile:" + testUserId;
        String statsKey = "stats:user:" + testUserId;
        Set<String> userKeys = new HashSet<>(Collections.singletonList(userKey));
        Set<String> statsKeys = new HashSet<>(Collections.singletonList(statsKey));

        when(redisTemplate.keys("user:*:" + testUserId)).thenReturn(userKeys);
        when(redisTemplate.keys("stats:user:" + testUserId)).thenReturn(statsKeys);

        redisUtil.invalidateUserCaches(testUserId);

        verify(redisTemplate).delete(userKeys);
        verify(redisTemplate).delete(statsKeys);
    }

    @Test
    void clearAllCaches_shouldDeleteAllKeys() {
        Set<String> allKeys = new HashSet<>();
        allKeys.add("key1");
        allKeys.add("key2");
        when(redisTemplate.keys("*")).thenReturn(allKeys);

        redisUtil.clearAllCaches();

        verify(redisTemplate).keys("*");
        verify(redisTemplate).delete(allKeys);
    }

    @Test
    void clearAllCaches_whenNoKeys_shouldNotCallDelete() {
        when(redisTemplate.keys("*")).thenReturn(Collections.emptySet());

        redisUtil.clearAllCaches();

        verify(redisTemplate).keys("*");
        verify(redisTemplate, never()).delete(anySet());
    }
}
