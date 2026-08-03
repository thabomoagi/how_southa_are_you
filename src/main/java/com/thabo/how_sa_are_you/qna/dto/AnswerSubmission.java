package com.thabo.how_sa_are_you.qna.dto;

import java.util.UUID;

public record AnswerSubmission(UUID questionId, UUID selectedOptionId) {
}