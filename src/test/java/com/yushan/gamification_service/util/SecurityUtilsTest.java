package com.yushan.gamification_service.util;

import com.yushan.gamification_service.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    private final UUID testUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // 在每个测试前设置模拟的安全上下文
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // 在每个测试后清理安全上下文，避免测试间相互影响
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldReturnUserId_whenUserIsAuthenticated() {
        // Given: 一个有效的认证和 CustomUserDetails
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn(testUserId.toString());

        // When: 调用 getCurrentUserId
        UUID result = SecurityUtils.getCurrentUserId();

        // Then: 应返回正确的 UUID
        assertEquals(testUserId, result);
    }

    @Test
    void getCurrentUserId_shouldThrowException_whenAuthenticationIsNull() {
        // Given: SecurityContext 返回 null 的 Authentication
        when(securityContext.getAuthentication()).thenReturn(null);

        // When & Then: 调用 getCurrentUserId 应抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class, SecurityUtils::getCurrentUserId);
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void getCurrentUserId_shouldThrowException_whenPrincipalIsNotCustomUserDetails() {
        // Given: Principal 是一个非 CustomUserDetails 对象
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new Object());

        // When & Then: 调用 getCurrentUserId 应抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class, SecurityUtils::getCurrentUserId);
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void getCurrentUserId_shouldThrowException_whenPrincipalIsNull() {
        // Given: Principal 为 null
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        // When & Then: 调用 getCurrentUserId 应抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class, SecurityUtils::getCurrentUserId);
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void getCurrentUserId_shouldThrowException_whenUserIdIsInvalidUUID() {
        // Given: userId 是一个无效的 UUID 字符串
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn("not-a-valid-uuid");

        // When & Then: 调用 getCurrentUserId 应因 UUID.fromString 失败而抛出异常
        assertThrows(IllegalArgumentException.class, SecurityUtils::getCurrentUserId);
    }
}
