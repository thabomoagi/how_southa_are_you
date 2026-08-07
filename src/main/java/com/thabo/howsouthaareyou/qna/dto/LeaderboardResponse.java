package com.thabo.howsouthaareyou.qna.dto;

import java.util.List;

public record LeaderboardResponse(
        String period,
        List<LeaderboardEntryDto> entries) {
}