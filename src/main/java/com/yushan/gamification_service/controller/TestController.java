package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.util.TestTokenGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test Token")
public class TestController {

    private final TestTokenGenerator testTokenGenerator;

    public TestController(TestTokenGenerator testTokenGenerator) {
        this.testTokenGenerator = testTokenGenerator;
    }

    @GetMapping("/generate-token")
    @Operation(summary = "Test Token", description = "JWT Token")
    public Map<String, String> generateToken(
            @RequestParam(required = false) String userId
    ) {
        UUID id;
        if (userId == null || userId.isEmpty()) {
            id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        } else {
            id = UUID.fromString(userId);
        }

        String token = testTokenGenerator.generate(id);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", id.toString());
        response.put("usage", "Authorize:Bearer " + token);

        return response;
    }

}