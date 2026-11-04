package com.gamehub.service;

import com.gamehub.dto.response.AchievementResponse;
import com.gamehub.model.*;
import com.gamehub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository,
                              UserAchievementRepository userAchievementRepository,
                              ScoreRepository scoreRepository,
                              UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(a -> AchievementResponse.fromEntity(a, false, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> getUserAchievements(Long userId) {
        List<UserAchievement> userAchs = userAchievementRepository.findAllWithAchievementByUserId(userId);
        Map<Integer, UserAchievement> unlockedMap = new HashMap<>();
        for (UserAchievement ua : userAchs) {
            unlockedMap.put(ua.getAchievement().getId(), ua);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return achievementRepository.findAll().stream()
                .map(a -> {
                    boolean unlocked = unlockedMap.containsKey(a.getId());
                    String dateStr = unlocked ? unlockedMap.get(a.getId()).getUnlockedAt().format(dtf) : null;
                    return AchievementResponse.fromEntity(a, unlocked, dateStr);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AchievementResponse> evaluateAndUnlock(User user, Game game, int score) {
        List<AchievementResponse> newUnlocks = new ArrayList<>();
        List<UserAchievement> existing = userAchievementRepository.findByUserId(user.getId());
        Set<Integer> unlockedIds = existing.stream()
                .map(ua -> ua.getAchievement().getId())
                .collect(Collectors.toSet());

        long totalSessions = scoreRepository.countByUserId(user.getId());
        long distinctGames = scoreRepository.countDistinctGamesPlayedByUserId(user.getId());

        List<Achievement> candidateAchievements = achievementRepository.findAll();

        for (Achievement ach : candidateAchievements) {
            if (unlockedIds.contains(ach.getId())) {
                continue;
            }

            boolean qualify = false;

            switch (ach.getCriteriaType()) {
                case SCORE_AT_LEAST:
                    if (ach.getGame() != null && ach.getGame().getId().equals(game.getId())) {
                        if (score >= ach.getCriteriaValue()) {
                            qualify = true;
                        }
                    }
                    break;
                case GAMES_PLAYED:
                    if (totalSessions >= ach.getCriteriaValue()) {
                        qualify = true;
                    }
                    break;
                case GAMES_TRIED:
                    if (distinctGames >= ach.getCriteriaValue()) {
                        qualify = true;
                    }
                    break;
                case STREAK_DAYS:
                    // Fallback for streak checks
                    break;
            }

            if (qualify) {
                UserAchievement ua = new UserAchievement(
                        new UserAchievementId(user.getId(), ach.getId()),
                        user,
                        ach,
                        LocalDateTime.now()
                    );
                userAchievementRepository.save(ua);

                // Award bonus XP for achievement unlock
                user.setTotalXp(user.getTotalXp() + ach.getXpReward());
                userRepository.save(user);

                newUnlocks.add(AchievementResponse.fromEntity(
                        ach, true, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                ));
            }
        }

        return newUnlocks;
    }
}
