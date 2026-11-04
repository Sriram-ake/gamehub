package com.gamehub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * GameHub Application Entry Point.
 * Multi-Game Arcade Platform - B.Tech IT Minor Project.
 */
@SpringBootApplication
public class GameHubApplication {

    private static final Logger log = LoggerFactory.getLogger(GameHubApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GameHubApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        String protocol = "http";
        String port = env.getProperty("server.port", "8080");
        String activeProfile = String.join(", ", env.getActiveProfiles());
        if (activeProfile.isEmpty()) {
            activeProfile = "dev";
        }

        log.info("\n----------------------------------------------------------\n\t" +
                 "🎮 GameHub Backend is running!\n\t" +
                 "Local URL:      {}://localhost:{}\n\t" +
                 "Health API:     {}://localhost:{}/api/health\n\t" +
                 "Swagger Docs:   {}://localhost:{}/swagger-ui.html\n\t" +
                 "H2 Console:     {}://localhost:{}/h2-console\n\t" +
                 "Active Profile: [{}]\n" +
                 "----------------------------------------------------------",
                 protocol, port,
                 protocol, port,
                 protocol, port,
                 protocol, port,
                 activeProfile);
    }
}
