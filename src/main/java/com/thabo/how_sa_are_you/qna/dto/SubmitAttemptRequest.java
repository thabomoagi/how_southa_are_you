package com.thabo.how_sa_are_you.qna.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.util.List;

public record SubmitAttemptRequest(
        @NotEmpty @Valid List<AnswerSubmission> answers) {
}