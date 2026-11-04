package com.gamehub.repository;

import com.gamehub.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Page<Score> findByUserIdOrderByPlayedAtDesc(Long userId, Pageable pageable);
    Page<Score> findByUserIdAndGameIdOrderByPlayedAtDesc(Long userId, Integer gameId, Pageable pageable);
    long countByUserId(Long userId);

    @Query("SELECT COUNT(DISTINCT s.game.id) FROM Score s WHERE s.user.id = :userId")
    long countDistinctGamesPlayedByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM Score s WHERE s.game.id = :gameId AND s.playedAt >= :since ORDER BY s.score DESC")
    List<Score> findRecentScoresByGame(@Param("gameId") Integer gameId, @Param("since") LocalDateTime since, Pageable pageable);
}
