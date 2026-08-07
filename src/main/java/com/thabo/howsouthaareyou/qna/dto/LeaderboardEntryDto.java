package com.thabo.howsouthaareyou.qna.dto;

import java.util.UUID;

public record LeaderboardEntryDto(
        Integer rank,
        UUID userId,
        String username,
        String profilePictureUrl,
        Integer score) {
}