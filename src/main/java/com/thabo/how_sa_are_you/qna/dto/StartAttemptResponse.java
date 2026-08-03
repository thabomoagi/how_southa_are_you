package com.thabo.how_sa_are_you.qna.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StartAttemptResponse(
        UUID attemptId,
        List<QuestionDto> questions,
        LocalDateTime startedAt,
        int timeLimitSeconds) {
}