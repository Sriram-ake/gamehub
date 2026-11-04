-- =============================================================================
-- GameHub: MySQL 8 Relational Schema
-- B.Tech IT Minor Project: Multi-Game Arcade Web Platform
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `gamehub_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `gamehub_db`;

-- -----------------------------------------------------------------------------
-- 1. Users Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `display_name` VARCHAR(80) DEFAULT NULL,
    `avatar_url` VARCHAR(255) DEFAULT '/assets/images/default-avatar.svg',
    `role` VARCHAR(20) NOT NULL DEFAULT 'PLAYER',
    `total_xp` INT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_login_at` TIMESTAMP NULL DEFAULT NULL,
    INDEX `idx_users_xp` (`total_xp` DESC)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. Games Registry Table (Plugin Architecture)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `games` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT NOT NULL,
    `thumbnail_url` VARCHAR(255) NOT NULL,
    `score_order` ENUM('HIGHER_IS_BETTER', 'LOWER_IS_BETTER') NOT NULL DEFAULT 'HIGHER_IS_BETTER',
    `max_score_per_second` DECIMAL(8,2) NOT NULL DEFAULT 10.00,
    `max_score` INT DEFAULT NULL,
    `global_weight` DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `sort_order` INT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_games_active_sort` (`is_active`, `sort_order`)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. Individual Game Sessions / Scores Table (Audit & History)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `scores` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `game_id` BIGINT NOT NULL,
    `score` INT NOT NULL,
    `duration_seconds` INT NOT NULL,
    `level_reached` INT NOT NULL DEFAULT 1,
    `played_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_scores_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_scores_game` FOREIGN KEY (`game_id`) REFERENCES `games` (`id`) ON DELETE CASCADE,
    INDEX `idx_scores_game_score` (`game_id`, `score`),
    INDEX `idx_scores_user_game_date` (`user_id`, `game_id`, `played_at`)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. High Scores Upsert Table (Fast O(1) Leaderboard Reads)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `high_scores` (
    `user_id` BIGINT NOT NULL,
    `game_id` BIGINT NOT NULL,
    `best_score` INT NOT NULL,
    `achieved_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `game_id`),
    CONSTRAINT `fk_high_scores_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_high_scores_game` FOREIGN KEY (`game_id`) REFERENCES `games` (`id`) ON DELETE CASCADE,
    INDEX `idx_high_scores_game_rank` (`game_id`, `best_score` DESC)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. Achievements Catalog Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `achievements` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `title` VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `icon` VARCHAR(50) NOT NULL DEFAULT 'trophy',
    `xp_reward` INT NOT NULL DEFAULT 50,
    `game_id` BIGINT DEFAULT NULL,
    `criteria_type` VARCHAR(50) NOT NULL,
    `criteria_value` INT NOT NULL,
    CONSTRAINT `fk_achievements_game` FOREIGN KEY (`game_id`) REFERENCES `games` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. User Unlocked Achievements Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_achievements` (
    `user_id` BIGINT NOT NULL,
    `achievement_id` BIGINT NOT NULL,
    `unlocked_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `achievement_id`),
    CONSTRAINT `fk_user_achievements_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_achievements_ach` FOREIGN KEY (`achievement_id`) REFERENCES `achievements` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
