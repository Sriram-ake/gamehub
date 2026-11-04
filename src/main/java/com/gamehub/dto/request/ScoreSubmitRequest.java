package com.gamehub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScoreSubmitRequest {

    @NotBlank(message = "Game slug is required")
    private String gameSlug;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be negative")
    private Integer score;

    @NotNull(message = "Duration in seconds is required")
    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer durationSeconds;

    @Min(value = 1, message = "Level reached must be at least 1")
    private Integer levelReached = 1;

    public ScoreSubmitRequest() {
    }

    public ScoreSubmitRequest(String gameSlug, Integer score, Integer durationSeconds, Integer levelReached) {
        this.gameSlug = gameSlug;
        this.score = score;
        this.durationSeconds = durationSeconds;
        this.levelReached = levelReached != null ? levelReached : 1;
    }

    public String getGameSlug() {
        return gameSlug;
    }

    public void setGameSlug(String gameSlug) {
        this.gameSlug = gameSlug;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getLevelReached() {
        return levelReached;
    }

    public void setLevelReached(Integer levelReached) {
        this.levelReached = levelReached;
    }
}
