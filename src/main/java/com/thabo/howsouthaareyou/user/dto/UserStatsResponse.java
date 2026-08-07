package com.thabo.howsouthaareyou.user.dto;

public record UserStatsResponse(
        long totalQnaAttempts,
        Integer qnaBestScore,
        Double qnaAverageScore,
        long totalThirtySecondsGames,
        Integer thirtySecondsBestScore,
        long totalGamesPlayed) {
}