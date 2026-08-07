package com.thabo.howsouthaareyou.thirtyseconds.repository;

import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsCard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThirtySecondsCardRepository extends JpaRepository<ThirtySecondsCard, Long> {

    boolean existsByExternalId(String externalId);

    @Query(value = "SELECT * FROM thirty_seconds_cards ORDER BY random()", nativeQuery = true)
    List<ThirtySecondsCard> findRandomCards(Pageable pageable);
}