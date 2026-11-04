package com.gamehub.dto.response;

public class LeaderboardEntry {
    private long rank;
    private Long userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private int score;
    private String achievedAt;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(long rank, Long userId, String username, String displayName,
                            String avatarUrl, int score, String achievedAt) {
        this.rank = rank;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.score = score;
        this.achievedAt = achievedAt;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(String achievedAt) {
        this.achievedAt = achievedAt;
    }
}
