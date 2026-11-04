package com.gamehub.controller;

import com.gamehub.dto.response.GameResponse;
import com.gamehub.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/games", "/api/v1/games"})
@Tag(name = "Games", description = "Endpoints for game catalog and metadata")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    @Operation(summary = "List all active games", description = "Returns active catalog games available on GameHub")
    public ResponseEntity<List<GameResponse>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllActiveGames());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get game details by slug", description = "Returns details of a specific game")
    public ResponseEntity<GameResponse> getGameBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(gameService.getGameBySlug(slug));
    }
}
