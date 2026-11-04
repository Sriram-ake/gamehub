package com.gamehub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private int score;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @Column(name = "level_reached")
    private int levelReached = 1;

    @CreationTimestamp
    @Column(name = "played_at", nullable = false, updatable = false)
    private LocalDateTime playedAt;

    public Score() {
    }

    public Score(Long id, User user, Game game, int score, int durationSeconds, int levelReached, LocalDateTime playedAt) {
        this.id = id;
        this.user = user;
        this.game = game;
        this.score = score;
        this.durationSeconds = durationSeconds;
        this.levelReached = levelReached;
        this.playedAt = playedAt;
    }

    public static ScoreBuilder builder() {
        return new ScoreBuilder();
    }

    public static class ScoreBuilder {
        private Long id;
        private User user;
        private Game game;
        private int score;
        private int durationSeconds;
        private int levelReached = 1;
        private LocalDateTime playedAt;

        ScoreBuilder() {
        }

        public ScoreBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ScoreBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ScoreBuilder game(Game game) {
            this.game = game;
            return this;
        }

        public ScoreBuilder score(int score) {
            this.score = score;
            return this;
        }

        public ScoreBuilder durationSeconds(int durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public ScoreBuilder levelReached(int levelReached) {
            this.levelReached = levelReached;
            return this;
        }

        public ScoreBuilder playedAt(LocalDateTime playedAt) {
            this.playedAt = playedAt;
            return this;
        }

        public Score build() {
            return new Score(id, user, game, score, durationSeconds, levelReached, playedAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getLevelReached() {
        return levelReached;
    }

    public void setLevelReached(int levelReached) {
        this.levelReached = levelReached;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}
