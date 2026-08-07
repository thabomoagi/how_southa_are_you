package com.thabo.howsouthaareyou.thirtyseconds.dto;

public record SubmitRoundScoreResponse(
        Long roundId,
        Integer roundNumber,
        String playerName,
        Integer score,
        boolean allRoundsCompleted) {
}