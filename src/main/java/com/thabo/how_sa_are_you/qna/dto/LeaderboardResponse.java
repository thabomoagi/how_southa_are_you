package com.thabo.how_sa_are_you.qna.dto;

import java.util.List;

public record LeaderboardResponse(
        List<LeaderboardEntryDto> entries,
        int page,
        int pageSize) {
}