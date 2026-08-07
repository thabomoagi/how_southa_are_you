package com.thabo.howsouthaareyou.qna.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AttemptResultResponse(
        UUID attemptId,
        Integer score,
        Integer correctCount,
        Integer totalQuestions,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        List<AnswerResultDto> results) {
}