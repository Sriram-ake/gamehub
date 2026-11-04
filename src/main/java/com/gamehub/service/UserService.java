package com.gamehub.service;

import com.gamehub.dto.response.UserSummaryResponse;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.model.User;
import com.gamehub.repository.ScoreRepository;
import com.gamehub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    public UserService(UserRepository userRepository, ScoreRepository scoreRepository) {
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserSummaryResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return UserSummaryResponse.fromEntity(user);
    }

    @Transactional
    public UserSummaryResponse updateProfile(Long userId, String displayName, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (displayName != null && !displayName.isBlank()) {
            user.setDisplayName(displayName.trim());
        }
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            user.setAvatarUrl(avatarUrl.trim());
        }

        userRepository.save(user);
        return UserSummaryResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        long totalSessions = scoreRepository.countByUserId(userId);
        long gamesPlayed = scoreRepository.countDistinctGamesPlayedByUserId(userId);
        int level = (int) Math.floor(Math.sqrt(user.getTotalXp() / 50.0)) + 1;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", totalSessions);
        stats.put("distinctGamesPlayed", gamesPlayed);
        stats.put("totalXp", user.getTotalXp());
        stats.put("level", level);
        stats.put("memberSince", user.getCreatedAt());

        return stats;
    }
}
