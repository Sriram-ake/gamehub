package com.gamehub.util;

import com.gamehub.exception.InvalidScoreException;
import com.gamehub.model.Game;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ScoreValidator {

    public void validateScore(Game game, int score, int durationSeconds) {
        if (score < 0) {
            throw new InvalidScoreException("Score cannot be negative: " + score);
        }

        if (durationSeconds <= 0) {
            throw new InvalidScoreException("Game session duration must be greater than 0 seconds");
        }

        if (score == 0) {
            return; // 0 score is always plausible
        }

        BigDecimal rate = BigDecimal.valueOf(score)
                .divide(BigDecimal.valueOf(durationSeconds), 2, RoundingMode.HALF_UP);

        if (rate.compareTo(game.getMaxScorePerSecond()) > 0) {
            throw new InvalidScoreException(String.format(
                    "Score submission rejected by anti-cheat check: rate of %s pts/sec exceeds maximum allowed %s pts/sec for %s",
                    rate, game.getMaxScorePerSecond(), game.getName()
            ));
        }
    }
}
