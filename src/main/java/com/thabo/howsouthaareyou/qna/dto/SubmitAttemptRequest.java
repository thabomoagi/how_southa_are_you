package com.thabo.howsouthaareyou.qna.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubmitAttemptRequest(
        @NotEmpty @Valid List<AnswerSubmission> answers) {
}