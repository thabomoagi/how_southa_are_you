package com.thabo.how_sa_are_you.qna.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateQuestionRequest(
        @NotBlank @Size(max = 500) String text,

        String category,

        @Size(min = 4, max = 4) @Valid List<CreateOptionRequest> options) {
}