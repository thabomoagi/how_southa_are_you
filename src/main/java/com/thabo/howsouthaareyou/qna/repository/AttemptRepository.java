package com.thabo.howsouthaareyou.qna.repository;

import com.thabo.howsouthaareyou.qna.entity.Attempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, UUID> {

    Optional<Attempt> findByIdAndUserId(UUID id, UUID userId);

    Page<Attempt> findByUserIdOrderByStartedAtDesc(UUID userId, Pageable pageable);

    long countByUserId(UUID userId);

    @Query("""
            SELECT COALESCE(MAX(a.score), 0)
            FROM Attempt a
            WHERE a.user.id = :userId
            """)
    Integer findBestScoreByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT COALESCE(AVG(a.score), 0.0)
            FROM Attempt a
            WHERE a.user.id = :userId
            AND a.score IS NOT NULL
            """)
    Double findAverageScoreByUserId(@Param("userId") UUID userId);
}