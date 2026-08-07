package com.thabo.howsouthaareyou.qna.repository;

import com.thabo.howsouthaareyou.qna.entity.Attempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaderboardRepository extends JpaRepository<Attempt, UUID> {

    @Query("""
            SELECT a.user.id AS userId,
                   a.user.username AS username,
                   a.user.profilePictureUrl AS profilePictureUrl,
                   MAX(a.score) AS score
            FROM Attempt a
            WHERE a.completedAt IS NOT NULL
            AND a.score IS NOT NULL
            GROUP BY a.user.id,
                     a.user.username,
                     a.user.profilePictureUrl
            ORDER BY MAX(a.score) DESC,
                     a.user.username ASC
            """)
    List<LeaderboardProjection> findTopAll(Pageable pageable);

    @Query("""
            SELECT a.user.id AS userId,
                   a.user.username AS username,
                   a.user.profilePictureUrl AS profilePictureUrl,
                   MAX(a.score) AS score
            FROM Attempt a
            WHERE a.completedAt IS NOT NULL
            AND a.score IS NOT NULL
            AND a.startedAt >= :startDate
            GROUP BY a.user.id,
                     a.user.username,
                     a.user.profilePictureUrl
            ORDER BY MAX(a.score) DESC,
                     a.user.username ASC
            """)
    List<LeaderboardProjection> findTopSince(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);
}