package com.gamehub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/test", "/api/v1/test"})
@Tag(name = "Health & Testing", description = "Test endpoint to verify API health")
public class TestController {

    @GetMapping
    @Operation(summary = "Health check test endpoint", description = "Returns system status and timestamp")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "GameHub Arcade Platform");
        response.put("message", "GameHub API is running successfully on localhost!");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
