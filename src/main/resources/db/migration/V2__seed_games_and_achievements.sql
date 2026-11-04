-- Seed Games
INSERT INTO games (slug, name, description, thumbnail, max_score_per_second, is_active)
VALUES
('snake', 'Snake', 'Classic retro snake. Eat apples, grow longer, avoid collisions!', '/assets/images/snake.png', 15.00, TRUE),
('breakout', 'Breakout', 'Destroy all bricks with the bouncing ball and paddle!', '/assets/images/breakout.png', 50.00, TRUE),
('flappy', 'Flappy Runner', 'Flap your wings to navigate pipes without crashing!', '/assets/images/flappy.png', 5.00, TRUE);

-- Seed Platform-wide and Game-specific Achievements
INSERT INTO achievements (code, title, description, icon, xp_reward, game_id, criteria_type, criteria_value)
VALUES
('FIRST_GAME', 'First Steps', 'Play your very first game on GameHub', '🎯', 25, NULL, 'GAMES_PLAYED', 1),
('GAMES_5', 'Getting Hooked', 'Play 5 game sessions across GameHub', '🔥', 50, NULL, 'GAMES_PLAYED', 5),
('EXPLORER', 'Explorer', 'Try all 3 available games on GameHub', '🧭', 100, NULL, 'GAMES_TRIED', 3),
('CENTURY', 'Century Club', 'Play 100 game sessions', '👑', 250, NULL, 'GAMES_PLAYED', 100),
('SNAKE_10', 'Appetizer', 'Score at least 10 points in Snake', '🍏', 30, (SELECT id FROM games WHERE slug = 'snake'), 'SCORE_AT_LEAST', 10),
('SNAKE_50', 'Snake Charmer', 'Score at least 50 points in Snake', '🐍', 100, (SELECT id FROM games WHERE slug = 'snake'), 'SCORE_AT_LEAST', 50),
('SNAKE_100', 'Python Master', 'Score at least 100 points in Snake', '🐉', 200, (SELECT id FROM games WHERE slug = 'snake'), 'SCORE_AT_LEAST', 100),
('BREAKOUT_50', 'Wall Chipper', 'Score at least 50 points in Breakout', '🧱', 40, (SELECT id FROM games WHERE slug = 'breakout'), 'SCORE_AT_LEAST', 50),
('BREAKOUT_200', 'Brick Breaker', 'Score at least 200 points in Breakout', '💥', 150, (SELECT id FROM games WHERE slug = 'breakout'), 'SCORE_AT_LEAST', 200),
('FLAPPY_10', 'Fledgling', 'Pass 10 pipes in Flappy Runner', '🐣', 50, (SELECT id FROM games WHERE slug = 'flappy'), 'SCORE_AT_LEAST', 10),
('FLAPPY_25', 'Sky Dancer', 'Pass 25 pipes in Flappy Runner', '🪶', 150, (SELECT id FROM games WHERE slug = 'flappy'), 'SCORE_AT_LEAST', 25);
