package com.thabo.howsouthaareyou.qna.dto;

public record AnswerResultDto(
        Long questionId,
        Long selectedOptionId,
        boolean correct,
        Long correctOptionId,
        String correctOptionText,
        Integer pointsEarned,
        Integer timeTakenMs) {
}