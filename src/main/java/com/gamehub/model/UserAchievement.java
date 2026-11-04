package com.gamehub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements")
public class UserAchievement {

    @EmbeddedId
    private UserAchievementId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("achievementId")
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @CreationTimestamp
    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    public UserAchievement() {
    }

    public UserAchievement(UserAchievementId id, User user, Achievement achievement, LocalDateTime unlockedAt) {
        this.id = id;
        this.user = user;
        this.achievement = achievement;
        this.unlockedAt = unlockedAt;
    }

    public static UserAchievementBuilder builder() {
        return new UserAchievementBuilder();
    }

    public static class UserAchievementBuilder {
        private UserAchievementId id;
        private User user;
        private Achievement achievement;
        private LocalDateTime unlockedAt;

        UserAchievementBuilder() {
        }

        public UserAchievementBuilder id(UserAchievementId id) {
            this.id = id;
            return this;
        }

        public UserAchievementBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserAchievementBuilder achievement(Achievement achievement) {
            this.achievement = achievement;
            return this;
        }

        public UserAchievementBuilder unlockedAt(LocalDateTime unlockedAt) {
            this.unlockedAt = unlockedAt;
            return this;
        }

        public UserAchievement build() {
            return new UserAchievement(id, user, achievement, unlockedAt);
        }
    }

    public UserAchievementId getId() {
        return id;
    }

    public void setId(UserAchievementId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
