package com.gamehub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 60)
    private String title;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String icon;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_type", nullable = false, length = 30)
    private CriteriaType criteriaType;

    @Column(name = "criteria_value", nullable = false)
    private int criteriaValue;

    public Achievement() {
    }

    public Achievement(Integer id, String code, String title, String description, String icon,
                       int xpReward, Game game, CriteriaType criteriaType, int criteriaValue) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.xpReward = xpReward;
        this.game = game;
        this.criteriaType = criteriaType;
        this.criteriaValue = criteriaValue;
    }

    public static AchievementBuilder builder() {
        return new AchievementBuilder();
    }

    public static class AchievementBuilder {
        private Integer id;
        private String code;
        private String title;
        private String description;
        private String icon;
        private int xpReward = 10;
        private Game game;
        private CriteriaType criteriaType;
        private int criteriaValue;

        AchievementBuilder() {
        }

        public AchievementBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public AchievementBuilder code(String code) {
            this.code = code;
            return this;
        }

        public AchievementBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AchievementBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AchievementBuilder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public AchievementBuilder xpReward(int xpReward) {
            this.xpReward = xpReward;
            return this;
        }

        public AchievementBuilder game(Game game) {
            this.game = game;
            return this;
        }

        public AchievementBuilder criteriaType(CriteriaType criteriaType) {
            this.criteriaType = criteriaType;
            return this;
        }

        public AchievementBuilder criteriaValue(int criteriaValue) {
            this.criteriaValue = criteriaValue;
            return this;
        }

        public Achievement build() {
            return new Achievement(id, code, title, description, icon, xpReward, game, criteriaType, criteriaValue);
        }
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public CriteriaType getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(CriteriaType criteriaType) {
        this.criteriaType = criteriaType;
    }

    public int getCriteriaValue() {
        return criteriaValue;
    }

    public void setCriteriaValue(int criteriaValue) {
        this.criteriaValue = criteriaValue;
    }
}
