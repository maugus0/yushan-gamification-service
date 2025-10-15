package com.yushan.gamification_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public Map health() {
        Map response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "gamification-service");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Gamification Service is running!");
        return response;
    }
}
