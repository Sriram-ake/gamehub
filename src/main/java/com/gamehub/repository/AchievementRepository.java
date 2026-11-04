package com.gamehub.repository;

import com.gamehub.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    Optional<Achievement> findByCode(String code);
    List<Achievement> findByGameId(Integer gameId);
    List<Achievement> findByGameIdIsNull();
}
