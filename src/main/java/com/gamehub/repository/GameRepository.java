package com.gamehub.repository;

import com.gamehub.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findBySlug(String slug);
    List<Game> findByIsActiveTrue();
    boolean existsBySlug(String slug);
}
