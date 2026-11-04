package com.gamehub.config;

import com.gamehub.model.*;
import com.gamehub.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final GameRepository gameRepository;
    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(GameRepository gameRepository,
                           AchievementRepository achievementRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.gameRepository = gameRepository;
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedGames();
        seedAchievements();
        seedDemoUsers();
    }

    private void seedGames() {
        if (gameRepository.count() > 0) return;

        log.info("Seeding initial games...");
        gameRepository.save(new Game(null, "snake", "Snake",
                "Classic retro snake with neon graphics, food bursts, and speed scaling.",
                "/assets/images/snake.png", BigDecimal.valueOf(15.00), true));

        gameRepository.save(new Game(null, "flappy", "Flappy Bird",
                "Flap through tricky pipes with realistic gravity and momentum physics.",
                "/assets/images/flappy.png", BigDecimal.valueOf(5.00), true));

        gameRepository.save(new Game(null, "2048", "2048",
                "Slide matching numbered tiles to merge and reach the legendary 2048 tile!",
                "/assets/images/2048.png", BigDecimal.valueOf(50.00), true));

        gameRepository.save(new Game(null, "tictactoe", "Tic Tac Toe",
                "Fast-paced classic grid battle against an intelligent AI opponent.",
                "/assets/images/tictactoe.png", BigDecimal.valueOf(10.00), true));
    }

    private void seedAchievements() {
        if (achievementRepository.count() > 0) return;

        log.info("Seeding initial achievements...");
        Game snake = gameRepository.findBySlug("snake").orElse(null);
        Game flappy = gameRepository.findBySlug("flappy").orElse(null);
        Game g2048 = gameRepository.findBySlug("2048").orElse(null);
        Game ttt = gameRepository.findBySlug("tictactoe").orElse(null);

        // Platform-wide
        achievementRepository.save(new Achievement(null, "FIRST_GAME", "First Steps",
                "Play your very first game on GameHub", "🎯", 25, null, CriteriaType.GAMES_PLAYED, 1));

        achievementRepository.save(new Achievement(null, "GAMES_5", "Arcade Regular",
                "Complete 5 game sessions across GameHub", "🔥", 50, null, CriteriaType.GAMES_PLAYED, 5));

        achievementRepository.save(new Achievement(null, "EXPLORER", "Explorer",
                "Try all 4 available games on GameHub", "🧭", 100, null, CriteriaType.GAMES_TRIED, 4));

        achievementRepository.save(new Achievement(null, "CENTURY", "Century Club",
                "Play 100 game sessions", "👑", 250, null, CriteriaType.GAMES_PLAYED, 100));

        // Game specific
        if (snake != null) {
            achievementRepository.save(new Achievement(null, "SNAKE_10", "Appetizer",
                    "Score at least 10 points in Snake", "🍏", 30, snake, CriteriaType.SCORE_AT_LEAST, 10));
            achievementRepository.save(new Achievement(null, "SNAKE_50", "Snake Charmer",
                    "Score at least 50 points in Snake", "🐍", 100, snake, CriteriaType.SCORE_AT_LEAST, 50));
        }

        if (flappy != null) {
            achievementRepository.save(new Achievement(null, "FLAPPY_10", "Fledgling",
                    "Pass 10 pipes in Flappy Bird", "🐣", 50, flappy, CriteriaType.SCORE_AT_LEAST, 10));
            achievementRepository.save(new Achievement(null, "FLAPPY_25", "Sky Dancer",
                    "Pass 25 pipes in Flappy Bird", "🪶", 150, flappy, CriteriaType.SCORE_AT_LEAST, 25));
        }

        if (g2048 != null) {
            achievementRepository.save(new Achievement(null, "TILE_512", "Math Wizard",
                    "Score at least 500 points in 2048", "🔢", 75, g2048, CriteriaType.SCORE_AT_LEAST, 500));
            achievementRepository.save(new Achievement(null, "TILE_2048", "2048 Master",
                    "Score at least 2000 points in 2048", "🏆", 200, g2048, CriteriaType.SCORE_AT_LEAST, 2000));
        }

        if (ttt != null) {
            achievementRepository.save(new Achievement(null, "TTT_WIN", "Tactician",
                    "Win a match in Tic Tac Toe", "❌", 40, ttt, CriteriaType.SCORE_AT_LEAST, 10));
        }
    }

    private void seedDemoUsers() {
        if (userRepository.count() > 0) return;

        log.info("Seeding demo players...");
        User demoPlayer = User.builder()
                .username("player")
                .email("player@gamehub.local")
                .passwordHash(passwordEncoder.encode("password123"))
                .displayName("RetroGamer")
                .avatarUrl("/assets/images/default-avatar.svg")
                .role(Role.PLAYER)
                .totalXp(120)
                .lastLoginAt(LocalDateTime.now())
                .build();
        userRepository.save(demoPlayer);

        User adminUser = User.builder()
                .username("admin")
                .email("admin@gamehub.local")
                .passwordHash(passwordEncoder.encode("adminpassword"))
                .displayName("GameHub Admin")
                .avatarUrl("/assets/images/default-avatar.svg")
                .role(Role.ADMIN)
                .totalXp(350)
                .lastLoginAt(LocalDateTime.now())
                .build();
        userRepository.save(adminUser);
    }
}
