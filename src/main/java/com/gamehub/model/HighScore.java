package com.gamehub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "high_scores", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_game", columnNames = {"user_id", "game_id"})
})
public class HighScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "best_score", nullable = false)
    private int bestScore;

    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;

    public HighScore() {
    }

    public HighScore(Long id, User user, Game game, int bestScore, LocalDateTime achievedAt) {
        this.id = id;
        this.user = user;
        this.game = game;
        this.bestScore = bestScore;
        this.achievedAt = achievedAt;
    }

    public static HighScoreBuilder builder() {
        return new HighScoreBuilder();
    }

    public static class HighScoreBuilder {
        private Long id;
        private User user;
        private Game game;
        private int bestScore;
        private LocalDateTime achievedAt;

        HighScoreBuilder() {
        }

        public HighScoreBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HighScoreBuilder user(User user) {
            this.user = user;
            return this;
        }

        public HighScoreBuilder game(Game game) {
            this.game = game;
            return this;
        }

        public HighScoreBuilder bestScore(int bestScore) {
            this.bestScore = bestScore;
            return this;
        }

        public HighScoreBuilder achievedAt(LocalDateTime achievedAt) {
            this.achievedAt = achievedAt;
            return this;
        }

        public HighScore build() {
            return new HighScore(id, user, game, bestScore, achievedAt);
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

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public LocalDateTime getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(LocalDateTime achievedAt) {
        this.achievedAt = achievedAt;
    }
}
