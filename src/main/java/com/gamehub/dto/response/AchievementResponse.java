package com.gamehub.dto.response;

import com.gamehub.model.Achievement;

public class AchievementResponse {
    private Integer id;
    private String code;
    private String title;
    private String description;
    private String icon;
    private int xpReward;
    private String gameSlug;
    private boolean unlocked;
    private String unlockedAt;

    public AchievementResponse() {
    }

    public AchievementResponse(Integer id, String code, String title, String description,
                               String icon, int xpReward, String gameSlug, boolean unlocked, String unlockedAt) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.xpReward = xpReward;
        this.gameSlug = gameSlug;
        this.unlocked = unlocked;
        this.unlockedAt = unlockedAt;
    }

    public static AchievementResponse fromEntity(Achievement achievement, boolean unlocked, String unlockedAt) {
        return new AchievementResponse(
                achievement.getId(),
                achievement.getCode(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.getIcon(),
                achievement.getXpReward(),
                achievement.getGame() != null ? achievement.getGame().getSlug() : null,
                unlocked,
                unlockedAt
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public String getGameSlug() {
        return gameSlug;
    }

    public void setGameSlug(String gameSlug) {
        this.gameSlug = gameSlug;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(String unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
