package com.gamehub.service;

import com.gamehub.dto.request.ScoreSubmitRequest;
import com.gamehub.dto.response.AchievementResponse;
import com.gamehub.dto.response.ScoreSubmitResponse;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.model.*;
import com.gamehub.repository.*;
import com.gamehub.util.ScoreValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ScoreService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final HighScoreRepository highScoreRepository;
    private final AchievementService achievementService;
    private final ScoreValidator scoreValidator;

    public ScoreService(GameRepository gameRepository,
                        UserRepository userRepository,
                        ScoreRepository scoreRepository,
                        HighScoreRepository highScoreRepository,
                        AchievementService achievementService,
                        ScoreValidator scoreValidator) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
        this.highScoreRepository = highScoreRepository;
        this.achievementService = achievementService;
        this.scoreValidator = scoreValidator;
    }

    @Transactional
    public ScoreSubmitResponse submitScore(Long userId, ScoreSubmitRequest request) {
        Game game = gameRepository.findBySlug(request.getGameSlug())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with slug: " + request.getGameSlug()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 1. Anti-cheat plausibility verification
        scoreValidator.validateScore(game, request.getScore(), request.getDurationSeconds());

        // 2. Insert into scores history
        Score scoreSession = Score.builder()
                .user(user)
                .game(game)
                .score(request.getScore())
                .durationSeconds(request.getDurationSeconds())
                .levelReached(request.getLevelReached())
                .build();
        Score savedScore = scoreRepository.save(scoreSession);

        // 3. Upsert High Score
        Optional<HighScore> existingHighScore = highScoreRepository.findByUserIdAndGameId(user.getId(), game.getId());
        boolean isNewHighScore = false;
        int previousBest = 0;

        if (existingHighScore.isPresent()) {
            HighScore hs = existingHighScore.get();
            previousBest = hs.getBestScore();
            if (request.getScore() > hs.getBestScore()) {
                hs.setBestScore(request.getScore());
                hs.setAchievedAt(LocalDateTime.now());
                highScoreRepository.save(hs);
                isNewHighScore = true;
            }
        } else {
            HighScore newHs = HighScore.builder()
                    .user(user)
                    .game(game)
                    .bestScore(request.getScore())
                    .achievedAt(LocalDateTime.now())
                    .build();
            highScoreRepository.save(newHs);
            isNewHighScore = true;
        }

        // 4. Award XP: 10 base XP + (score / 10)
        int xpEarned = 10 + (request.getScore() / 10);
        user.setTotalXp(user.getTotalXp() + xpEarned);
        userRepository.save(user);

        // 5. Evaluate and unlock achievements
        List<AchievementResponse> newAchievements = achievementService.evaluateAndUnlock(user, game, request.getScore());

        // 6. Compute user rank for this game
        long rank = highScoreRepository.findRankByGameIdAndScore(game.getId(), Math.max(request.getScore(), previousBest));
        int level = (int) Math.floor(Math.sqrt(user.getTotalXp() / 50.0)) + 1;

        return new ScoreSubmitResponse(
                savedScore.getId(),
                request.getScore(),
                isNewHighScore,
                previousBest,
                rank,
                xpEarned,
                user.getTotalXp(),
                level,
                newAchievements
        );
    }

    @Transactional(readOnly = true)
    public Page<Score> getUserScoreHistory(Long userId, Pageable pageable) {
        return scoreRepository.findByUserIdOrderByPlayedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getUserBestScores(Long userId) {
        List<HighScore> highScores = highScoreRepository.findByUserId(userId);
        Map<String, Integer> bestMap = new HashMap<>();
        for (HighScore hs : highScores) {
            bestMap.put(hs.getGame().getSlug(), hs.getBestScore());
        }
        return bestMap;
    }

    @Transactional(readOnly = true)
    public int getUserBestScoreForGame(Long userId, String gameSlug) {
        Game game = gameRepository.findBySlug(gameSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameSlug));
        return highScoreRepository.findByUserIdAndGameId(userId, game.getId())
                .map(HighScore::getBestScore)
                .orElse(0);
    }
}
