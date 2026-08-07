package com.thabo.howsouthaareyou.qna.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AnswerSubmission(
        @NotNull Long questionId,
        Long selectedOptionId,
        @NotNull @Min(0) Integer timeTakenMs) {
}