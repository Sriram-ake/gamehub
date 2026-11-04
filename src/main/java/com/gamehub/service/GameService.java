package com.gamehub.service;

import com.gamehub.dto.response.GameResponse;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.model.Game;
import com.gamehub.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public List<GameResponse> getAllActiveGames() {
        return gameRepository.findByIsActiveTrue().stream()
                .map(GameResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GameResponse getGameBySlug(String slug) {
        Game game = gameRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with slug: " + slug));
        return GameResponse.fromEntity(game);
    }
}
