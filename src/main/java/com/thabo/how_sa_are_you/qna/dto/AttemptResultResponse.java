package com.thabo.how_sa_are_you.qna.dto;

import java.util.List;
import java.util.UUID;

public record AttemptResultResponse(
        UUID attemptId,
        int score,
        int totalQuestions,
        long tookMs,
        List<AnswerResultDto> results) {
}