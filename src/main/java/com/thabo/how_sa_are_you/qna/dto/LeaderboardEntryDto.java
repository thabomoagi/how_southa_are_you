package com.thabo.how_sa_are_you.qna.dto;

public record LeaderboardEntryDto(
        int rank,
        String username,
        int bestScore) {
}