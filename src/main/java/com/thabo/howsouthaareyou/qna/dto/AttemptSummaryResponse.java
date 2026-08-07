package com.thabo.howsouthaareyou.qna.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttemptSummaryResponse(
        UUID attemptId,
        Integer score,
        Integer correctCount,
        Integer totalQuestions,
        LocalDateTime startedAt,
        LocalDateTime completedAt) {
}