package com.thabo.howsouthaareyou.thirtyseconds.repository;

import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThirtySecondsRoundRepository extends JpaRepository<ThirtySecondsRound, Long> {

    List<ThirtySecondsRound> findByGameIdOrderByRoundNumberAsc(UUID gameId);

    Optional<ThirtySecondsRound> findByGameIdAndRoundNumber(UUID gameId, Integer roundNumber);

    boolean existsByGameIdAndRoundNumber(UUID gameId, Integer roundNumber);
}