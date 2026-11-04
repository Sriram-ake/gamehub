package com.gamehub.service;

import com.gamehub.dto.response.LeaderboardEntry;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.model.Game;
import com.gamehub.model.HighScore;
import com.gamehub.repository.GameRepository;
import com.gamehub.repository.HighScoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {

    private final HighScoreRepository highScoreRepository;
    private final GameRepository gameRepository;

    public LeaderboardService(HighScoreRepository highScoreRepository, GameRepository gameRepository) {
        this.highScoreRepository = highScoreRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getGameLeaderboard(String slug, int limit) {
        Game game = gameRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with slug: " + slug));

        int clampedLimit = Math.min(Math.max(limit, 1), 100);
        List<HighScore> topScores = highScoreRepository.findTopByGameIdOrderByBestScoreDesc(
                game.getId(), PageRequest.of(0, clampedLimit));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<LeaderboardEntry> entries = new ArrayList<>();
        long rank = 1;

        for (HighScore hs : topScores) {
            entries.add(new LeaderboardEntry(
                    rank++,
                    hs.getUser().getId(),
                    hs.getUser().getUsername(),
                    hs.getUser().getDisplayName() != null ? hs.getUser().getDisplayName() : hs.getUser().getUsername(),
                    hs.getUser().getAvatarUrl() != null ? hs.getUser().getAvatarUrl() : "/assets/images/default-avatar.svg",
                    hs.getBestScore(),
                    hs.getAchievedAt().format(dtf)
            ));
        }

        return entries;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getGlobalLeaderboard(int limit) {
        int clampedLimit = Math.min(Math.max(limit, 1), 100);
        List<Object[]> rows = highScoreRepository.findGlobalLeaderboard(PageRequest.of(0, clampedLimit));

        List<LeaderboardEntry> entries = new ArrayList<>();
        long rank = 1;

        for (Object[] row : rows) {
            Long userId = (Long) row[0];
            String username = (String) row[1];
            String displayName = (String) row[2];
            String avatarUrl = (String) row[3];
            Number totalPoints = (Number) row[4];

            entries.add(new LeaderboardEntry(
                    rank++,
                    userId,
                    username,
                    displayName != null ? displayName : username,
                    avatarUrl != null ? avatarUrl : "/assets/images/default-avatar.svg",
                    totalPoints != null ? totalPoints.intValue() : 0,
                    "-"
            ));
        }

        return entries;
    }
}
