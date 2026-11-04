package com.gamehub.controller;

import com.gamehub.dto.response.LeaderboardEntry;
import com.gamehub.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/leaderboards", "/api/v1/leaderboards"})
@Tag(name = "Leaderboards", description = "Endpoints for per-game and global leaderboards")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/global")
    @Operation(summary = "Get global leaderboard", description = "Returns top players ranked by total points across all games")
    public ResponseEntity<List<LeaderboardEntry>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(limit));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get per-game leaderboard", description = "Returns top players ranked by best score for a specific game")
    public ResponseEntity<List<LeaderboardEntry>> getGameLeaderboard(
            @PathVariable String slug,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getGameLeaderboard(slug, limit));
    }
}
