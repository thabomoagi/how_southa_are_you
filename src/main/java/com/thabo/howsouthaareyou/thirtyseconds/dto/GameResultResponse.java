package com.thabo.howsouthaareyou.thirtyseconds.dto;

import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GameResultResponse(
        UUID gameId,
        ThirtySecondsMode mode,
        Integer playerCount,
        Integer totalScore,
        String winningPlayerName,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        List<PlayerScoreDto> playerScores,
        List<RoundDto> rounds) {
}