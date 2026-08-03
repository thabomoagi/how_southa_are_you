package com.thabo.how_sa_are_you.qna.repository;

import com.thabo.how_sa_are_you.qna.entity.Attempt;
import com.thabo.how_sa_are_you.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AttemptRepository extends JpaRepository<Attempt, UUID> {

    List<Attempt> findByUser(User user);

    @Query("""
                SELECT a.user.username AS username, MAX(a.score) AS bestScore
                FROM Attempt a
                WHERE a.completedAt IS NOT NULL
                GROUP BY a.user.username
                ORDER BY MAX(a.score) DESC
            """)
    List<LeaderboardRow> findLeaderboard(Pageable pageable);

}