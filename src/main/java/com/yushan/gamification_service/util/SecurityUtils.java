// src/main/java/com/yushan/gamification_service/util/SecurityUtils.java
package com.yushan.gamification_service.util;

import com.yushan.gamification_service.exception.UnauthorizedException;
import com.yushan.gamification_service.security.CustomUserDetails; // 1. 导入正确的类
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            String userIdStr = ((CustomUserDetails) principal).getUserId();
            try {
                return UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                throw new UnauthorizedException("Invalid user ID format in token.");
            }
        }

        throw new UnauthorizedException("User principal is not of expected type.");
    }
}