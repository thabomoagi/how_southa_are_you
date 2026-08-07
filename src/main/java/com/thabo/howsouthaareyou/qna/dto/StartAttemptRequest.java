package com.thabo.howsouthaareyou.qna.dto;

import com.thabo.howsouthaareyou.qna.entity.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record StartAttemptRequest(
        Long categoryId,
        Difficulty difficulty,
        @Min(1) @Max(20) Integer questionCount,
        @Min(30) @Max(600) Integer durationSeconds) {
}