package com.thabo.howsouthaareyou.thirtyseconds.dto;

import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StartGameResponse(
        UUID gameId,
        ThirtySecondsMode mode,
        Integer playerCount,
        Integer roundsPerPlayer,
        Integer totalRounds,
        Integer durationSeconds,
        LocalDateTime startedAt,
        List<RoundDto> rounds) {
}