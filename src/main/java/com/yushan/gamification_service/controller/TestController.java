package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.dto.ApiResponse;
import com.yushan.gamification_service.util.JwtTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Controller for generating JWT tokens
 * This is only for development/testing purposes
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JwtTestUtil jwtTestUtil;

    /**
     * Generate test JWT token for USER role
     */
    @GetMapping("/token/user")
    public ApiResponse<Map<String, String>> getUserToken() {
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("token", jwtTestUtil.generateTestUserToken());
        tokenData.put("role", "USER");
        tokenData.put("message", "Use this token in Authorization header: Bearer <token>");
        return ApiResponse.success("USER token generated successfully", tokenData);
    }

    /**
     * Generate test JWT token for ADMIN role
     */
    @GetMapping("/token/admin")
    public ApiResponse<Map<String, String>> getAdminToken() {
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("token", jwtTestUtil.generateTestAdminToken());
        tokenData.put("role", "ADMIN");
        tokenData.put("message", "Use this token in Authorization header: Bearer <token>");
        return ApiResponse.success("ADMIN token generated successfully", tokenData);
    }

    /**
     * Generate test JWT token for suspended user
     */
    @GetMapping("/token/suspended")
    public ApiResponse<Map<String, String>> getSuspendedToken() {
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("token", jwtTestUtil.generateTestSuspendedToken());
        tokenData.put("role", "USER (SUSPENDED)");
        tokenData.put("message", "This token should be rejected due to suspended status");
        return ApiResponse.success("SUSPENDED token generated successfully", tokenData);
    }
}