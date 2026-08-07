package com.thabo.howsouthaareyou.qna.dto;

import com.thabo.howsouthaareyou.qna.entity.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateQuestionRequest(
        @NotNull Long categoryId,
        @NotBlank String prompt,
        @NotNull Difficulty difficulty,
        @NotEmpty @Valid List<CreateOptionRequest> options) {
}