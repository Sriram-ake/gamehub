package com.gamehub.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ScoreSubmitResponse {
    private Long scoreId;
    private int score;
    private boolean isNewHighScore;
    private int previousBest;
    private long rank;
    private int xpEarned;
    private int totalXp;
    private int level;
    private List<AchievementResponse> newAchievements = new ArrayList<>();

    public ScoreSubmitResponse() {
    }

    public ScoreSubmitResponse(Long scoreId, int score, boolean isNewHighScore, int previousBest,
                               long rank, int xpEarned, int totalXp, int level, List<AchievementResponse> newAchievements) {
        this.scoreId = scoreId;
        this.score = score;
        this.isNewHighScore = isNewHighScore;
        this.previousBest = previousBest;
        this.rank = rank;
        this.xpEarned = xpEarned;
        this.totalXp = totalXp;
        this.level = level;
        this.newAchievements = newAchievements != null ? newAchievements : new ArrayList<>();
    }

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isNewHighScore() {
        return isNewHighScore;
    }

    public void setNewHighScore(boolean newHighScore) {
        isNewHighScore = newHighScore;
    }

    public int getPreviousBest() {
        return previousBest;
    }

    public void setPreviousBest(int previousBest) {
        this.previousBest = previousBest;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<AchievementResponse> getNewAchievements() {
        return newAchievements;
    }

    public void setNewAchievements(List<AchievementResponse> newAchievements) {
        this.newAchievements = newAchievements;
    }
}
