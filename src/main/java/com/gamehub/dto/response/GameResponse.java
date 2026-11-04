package com.gamehub.dto.response;

import com.gamehub.model.Game;

import java.math.BigDecimal;

public class GameResponse {
    private Integer id;
    private String slug;
    private String name;
    private String description;
    private String thumbnail;
    private BigDecimal maxScorePerSecond;
    private boolean active;

    public GameResponse() {
    }

    public GameResponse(Integer id, String slug, String name, String description,
                        String thumbnail, BigDecimal maxScorePerSecond, boolean active) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.maxScorePerSecond = maxScorePerSecond;
        this.active = active;
    }

    public static GameResponse fromEntity(Game game) {
        return new GameResponse(
                game.getId(),
                game.getSlug(),
                game.getName(),
                game.getDescription(),
                game.getThumbnail(),
                game.getMaxScorePerSecond(),
                game.isActive()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public BigDecimal getMaxScorePerSecond() {
        return maxScorePerSecond;
    }

    public void setMaxScorePerSecond(BigDecimal maxScorePerSecond) {
        this.maxScorePerSecond = maxScorePerSecond;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
