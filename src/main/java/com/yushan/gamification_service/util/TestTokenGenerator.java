package com.yushan.gamification_service.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TestTokenGenerator {

    private final JwtUtil jwtUtil;

    public TestTokenGenerator(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generate(UUID userId) {
        return jwtUtil.generateToken(userId);
    }
}