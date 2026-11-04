package com.gamehub.controller;

import com.gamehub.dto.response.AchievementResponse;
import com.gamehub.security.UserPrincipal;
import com.gamehub.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/achievements", "/api/v1/achievements"})
@Tag(name = "Achievements", description = "Endpoints for badges and player progress")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    @Operation(summary = "Get all achievement definitions", description = "Returns full list of unlockable platform and game achievements")
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user achievements", description = "Returns achievements showing unlocked status and timestamps for current user")
    public ResponseEntity<List<AchievementResponse>> getMyAchievements(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(achievementService.getUserAchievements(principal.getId()));
    }
}
