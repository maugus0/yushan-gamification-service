package com.yushan.gamification_service.util;

import com.yushan.gamification_service.exception.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            String userIdStr = ((User) principal).getUsername();
            try {
                return UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                throw new UnauthorizedException("Invalid user ID format in token.");
            }
        }

        throw new UnauthorizedException("User is not authenticated or token is invalid.");
    }
}