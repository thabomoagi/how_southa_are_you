package com.thabo.howsouthaareyou.thirtyseconds.repository;

import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThirtySecondsGameRepository extends JpaRepository<ThirtySecondsGame, UUID> {

    Optional<ThirtySecondsGame> findByIdAndUserId(UUID id, UUID userId);

    Page<ThirtySecondsGame> findByUserIdOrderByStartedAtDesc(UUID userId, Pageable pageable);

    long countByUserId(UUID userId);

    @Query("""
            SELECT COALESCE(MAX(g.totalScore), 0)
            FROM ThirtySecondsGame g
            WHERE g.user.id = :userId
            AND g.totalScore IS NOT NULL
            """)
    Integer findBestScoreByUserId(@Param("userId") UUID userId);
}