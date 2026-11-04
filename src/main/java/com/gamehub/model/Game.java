package com.gamehub.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String slug;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String thumbnail;

    @Column(name = "max_score_per_second", nullable = false, precision = 8, scale = 2)
    private BigDecimal maxScorePerSecond;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public Game() {
    }

    public Game(Integer id, String slug, String name, String description, String thumbnail,
                BigDecimal maxScorePerSecond, boolean isActive) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.maxScorePerSecond = maxScorePerSecond;
        this.isActive = isActive;
    }

    public static GameBuilder builder() {
        return new GameBuilder();
    }

    public static class GameBuilder {
        private Integer id;
        private String slug;
        private String name;
        private String description;
        private String thumbnail;
        private BigDecimal maxScorePerSecond;
        private boolean isActive = true;

        GameBuilder() {
        }

        public GameBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public GameBuilder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public GameBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GameBuilder description(String description) {
            this.description = description;
            return this;
        }

        public GameBuilder thumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public GameBuilder maxScorePerSecond(BigDecimal maxScorePerSecond) {
            this.maxScorePerSecond = maxScorePerSecond;
            return this;
        }

        public GameBuilder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Game build() {
            return new Game(id, slug, name, description, thumbnail, maxScorePerSecond, isActive);
        }
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
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
