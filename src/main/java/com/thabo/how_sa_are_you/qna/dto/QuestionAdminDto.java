package com.thabo.how_sa_are_you.qna.dto;

import java.util.List;
import java.util.UUID;

public record QuestionAdminDto(
        UUID id,
        String text,
        String category,
        boolean active,
        List<OptionAdminDto> options) {
}