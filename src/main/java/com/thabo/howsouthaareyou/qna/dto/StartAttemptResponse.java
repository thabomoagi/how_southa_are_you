package com.thabo.howsouthaareyou.qna.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StartAttemptResponse(
        UUID attemptId,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        Integer durationSeconds,
        Integer totalQuestions,
        List<QuestionDto> questions) {
}