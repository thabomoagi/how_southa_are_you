package com.thabo.how_sa_are_you.qna.dto;

import java.util.UUID;

public record AnswerResultDto(
        UUID questionId,
        UUID selectedOptionId,
        UUID correctOptionId,
        boolean correct) {
}