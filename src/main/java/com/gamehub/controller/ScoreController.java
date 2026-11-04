package com.gamehub.controller;

import com.gamehub.dto.request.ScoreSubmitRequest;
import com.gamehub.dto.response.ScoreSubmitResponse;
import com.gamehub.model.Score;
import com.gamehub.security.UserPrincipal;
import com.gamehub.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/scores", "/api/v1/scores"})
@Tag(name = "Scores", description = "Endpoints for game session score submission and history")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping
    @Operation(summary = "Submit a game session score", description = "Validates anti-cheat bounds, saves score, updates personal best, awards XP, and unlocks badges.")
    public ResponseEntity<ScoreSubmitResponse> submitScore(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ScoreSubmitRequest request) {
        ScoreSubmitResponse response = scoreService.submitScore(principal.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get user game session history", description = "Returns paginated list of past game sessions for current player")
    public ResponseEntity<Page<Score>> getMyScores(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(scoreService.getUserScoreHistory(principal.getId(), PageRequest.of(page, size)));
    }

    @GetMapping("/me/best")
    @Operation(summary = "Get personal best score for every game")
    public ResponseEntity<Map<String, Integer>> getMyBestScores(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(scoreService.getUserBestScores(principal.getId()));
    }

    @GetMapping("/me/best/{slug}")
    @Operation(summary = "Get personal best score for a specific game")
    public ResponseEntity<Integer> getMyBestScoreForGame(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String slug) {
        return ResponseEntity.ok(scoreService.getUserBestScoreForGame(principal.getId(), slug));
    }
}
