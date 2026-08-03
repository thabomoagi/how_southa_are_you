package com.thabo.how_sa_are_you.qna.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOptionRequest(
        @NotBlank String text,
        boolean correct) {
}