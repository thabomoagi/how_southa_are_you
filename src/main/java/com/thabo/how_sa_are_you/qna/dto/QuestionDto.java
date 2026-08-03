package com.thabo.how_sa_are_you.qna.dto;

import java.util.List;
import java.util.UUID;

public record QuestionDto(UUID id, String text, String category, List<OptionDto> options) {
}