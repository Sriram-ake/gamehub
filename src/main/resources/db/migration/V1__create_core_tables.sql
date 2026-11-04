CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(30)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(50),
    avatar_url    VARCHAR(255),
    role          VARCHAR(20)  NOT NULL DEFAULT 'PLAYER',
    total_xp      INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP    NULL
);

CREATE TABLE games (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    slug                 VARCHAR(30)   NOT NULL UNIQUE,
    name                 VARCHAR(60)   NOT NULL,
    description          VARCHAR(255),
    thumbnail            VARCHAR(255),
    max_score_per_second DECIMAL(8,2)  NOT NULL,
    is_active            BOOLEAN       NOT NULL DEFAULT TRUE
);

CREATE TABLE scores (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT    NOT NULL,
    game_id          INT       NOT NULL,
    score            INT       NOT NULL,
    duration_seconds INT       NOT NULL,
    level_reached    INT       DEFAULT 1,
    played_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scores_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_scores_game FOREIGN KEY (game_id) REFERENCES games(id)
);

CREATE TABLE high_scores (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT    NOT NULL,
    game_id     INT       NOT NULL,
    best_score  INT       NOT NULL,
    achieved_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_user_game UNIQUE (user_id, game_id),
    CONSTRAINT fk_high_scores_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_high_scores_game FOREIGN KEY (game_id) REFERENCES games(id)
);

CREATE TABLE achievements (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(40)  NOT NULL UNIQUE,
    title          VARCHAR(60)  NOT NULL,
    description    VARCHAR(255),
    icon           VARCHAR(255),
    xp_reward      INT          NOT NULL DEFAULT 10,
    game_id        INT          NULL,
    criteria_type  VARCHAR(30)  NOT NULL,
    criteria_value INT          NOT NULL,
    CONSTRAINT fk_achievements_game FOREIGN KEY (game_id) REFERENCES games(id)
);

CREATE TABLE user_achievements (
    user_id        BIGINT    NOT NULL,
    achievement_id INT       NOT NULL,
    unlocked_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, achievement_id),
    CONSTRAINT fk_user_achievements_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_achievements_ach FOREIGN KEY (achievement_id) REFERENCES achievements(id)
);

CREATE INDEX idx_hs_game_score ON high_scores (game_id, best_score DESC);
CREATE INDEX idx_scores_user_game ON scores (user_id, game_id, played_at);
CREATE INDEX idx_scores_played ON scores (played_at);
