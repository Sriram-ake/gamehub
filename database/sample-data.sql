-- =============================================================================
-- GameHub: Initial Seed Data Script
-- B.Tech IT Minor Project: 4 Launch Games, Achievements & Demo Accounts
-- =============================================================================

USE `gamehub_db`;

-- -----------------------------------------------------------------------------
-- Seed 4 Initial Games (Data-Driven Architecture)
-- -----------------------------------------------------------------------------
INSERT INTO `games` (`code`, `name`, `description`, `thumbnail_url`, `score_order`, `max_score_per_second`, `max_score`, `global_weight`, `is_active`, `sort_order`)
VALUES
('snake', 'Snake', 'Retro neon snake with food bursts, rare golden apples, and dynamic velocity scaling.', '/assets/images/snake.png', 'HIGHER_IS_BETTER', 15.00, 99999, 1.00, TRUE, 1),
('flappy', 'Flappy Bird', 'Navigate tricky glowing pipe gates with realistic gravity and momentum physics.', '/assets/images/flappy.png', 'HIGHER_IS_BETTER', 5.00, 9999, 1.20, TRUE, 2),
('2048', '2048', 'Slide and merge matching numbered tiles to conquer the mathematical summit of 2048.', '/assets/images/2048.png', 'HIGHER_IS_BETTER', 50.00, 131072, 1.00, TRUE, 3),
('tictactoe', 'Tic Tac Toe', 'Fast-paced tactical 3x3 grid battle against a Minimax AI opponent.', '/assets/images/tictactoe.png', 'HIGHER_IS_BETTER', 10.00, 500, 0.80, TRUE, 4)
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- -----------------------------------------------------------------------------
-- Seed Demo Accounts (Password: password123 and adminpassword)
-- -----------------------------------------------------------------------------
INSERT INTO `users` (`username`, `email`, `password_hash`, `display_name`, `avatar_url`, `role`, `total_xp`)
VALUES
('player', 'player@gamehub.local', '$2a$10$V1a7L0pQdE.x5gVzK0W8reK8P1s5cW3a6yB1n0iJ4k8h2g5X7yZ.e', 'RetroGamer', '/assets/images/default-avatar.svg', 'PLAYER', 120),
('admin', 'admin@gamehub.local', '$2a$10$W2b8M1qReF.y6hW0L1X9sfL9Q2t6dX4b7zC2o1jK5l9i3h6Y8z0.f', 'SystemAdmin', '/assets/images/default-avatar.svg', 'ADMIN', 500)
ON DUPLICATE KEY UPDATE `display_name`=VALUES(`display_name`);

-- -----------------------------------------------------------------------------
-- Seed Catalog Achievements
-- -----------------------------------------------------------------------------
INSERT INTO `achievements` (`code`, `title`, `description`, `icon`, `xp_reward`, `game_id`, `criteria_type`, `criteria_value`)
VALUES
('FIRST_GAME', 'First Steps', 'Play your very first game on GameHub', 'award', 25, NULL, 'GAMES_PLAYED', 1),
('VETERAN', 'Arcade Veteran', 'Complete 25 total gaming sessions', 'zap', 100, NULL, 'GAMES_PLAYED', 25),
('ALL_ROUNDER', 'All Rounder', 'Try all 4 distinct arcade games', 'compass', 150, NULL, 'GAMES_TRIED', 4),

-- Snake Achievements
('SNAKE_10', 'Appetizer', 'Score at least 10 points in Snake', 'target', 30, 1, 'SCORE_AT_LEAST', 10),
('SNAKE_50', 'Snake Charmer', 'Score at least 50 points in Snake', 'star', 100, 1, 'SCORE_AT_LEAST', 50),
('SNAKE_100', 'Python Master', 'Score at least 100 points in Snake', 'crown', 250, 1, 'SCORE_AT_LEAST', 100),

-- Flappy Bird Achievements
('FLAPPY_5', 'First Flight', 'Clear 5 pipes in Flappy Bird', 'feather', 30, 2, 'SCORE_AT_LEAST', 5),
('FLAPPY_20', 'Sky Ace', 'Clear 20 pipes in Flappy Bird', 'shield', 120, 2, 'SCORE_AT_LEAST', 20),

-- 2048 Achievements
('2048_TILE_512', 'Halfway There', 'Reach score 5,000 in 2048', 'grid', 80, 3, 'SCORE_AT_LEAST', 5000),
('2048_TILE_2048', 'Legendary 2048', 'Score over 20,000 in 2048', 'trophy', 300, 3, 'SCORE_AT_LEAST', 20000),

-- Tic Tac Toe Achievements
('TTT_FIRST_WIN', 'Tactical Mind', 'Win your first match against the AI in Tic Tac Toe', 'check-circle', 50, 4, 'SCORE_AT_LEAST', 1)
ON DUPLICATE KEY UPDATE `title`=VALUES(`title`);
