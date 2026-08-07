package com.thabo.howsouthaareyou.qna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOptionRequest(
        @NotBlank String optionText,
        @NotNull Boolean correct) {
}