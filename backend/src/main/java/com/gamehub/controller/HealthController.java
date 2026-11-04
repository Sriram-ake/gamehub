package com.gamehub.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health & Diagnostic Controller.
 * Verifies backend availability and provides server runtime info for viva demos.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    private final Environment environment;

    public HealthController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping({"/health", "/test"})
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "GameHub Arcade Platform");
        response.put("message", "GameHub Spring Boot backend is running successfully!");
        response.put("activeProfile", environment.getActiveProfiles().length > 0 
                ? environment.getActiveProfiles()[0] : "dev");
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
