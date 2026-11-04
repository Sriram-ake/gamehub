package com.gamehub.repository;

import com.gamehub.model.HighScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HighScoreRepository extends JpaRepository<HighScore, Long> {

    Optional<HighScore> findByUserIdAndGameId(Long userId, Integer gameId);

    List<HighScore> findByUserId(Long userId);

    @Query("SELECT h FROM HighScore h JOIN FETCH h.user WHERE h.game.id = :gameId ORDER BY h.bestScore DESC")
    List<HighScore> findTopByGameIdOrderByBestScoreDesc(@Param("gameId") Integer gameId, Pageable pageable);

    @Query("SELECT COUNT(h) + 1 FROM HighScore h WHERE h.game.id = :gameId AND h.bestScore > :score")
    long findRankByGameIdAndScore(@Param("gameId") Integer gameId, @Param("score") int score);

    @Query("SELECT h.user.id AS userId, h.user.username AS username, h.user.displayName AS displayName, " +
           "h.user.avatarUrl AS avatarUrl, SUM(h.bestScore) AS totalPoints " +
           "FROM HighScore h GROUP BY h.user.id, h.user.username, h.user.displayName, h.user.avatarUrl " +
           "ORDER BY totalPoints DESC")
    List<Object[]> findGlobalLeaderboard(Pageable pageable);
}
