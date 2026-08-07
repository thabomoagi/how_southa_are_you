package com.thabo.howsouthaareyou.qna.dto;

import com.thabo.howsouthaareyou.qna.entity.Difficulty;

import java.util.List;

public record QuestionAdminDto(
        Long id,
        Long categoryId,
        String categoryName,
        String prompt,
        Difficulty difficulty,
        Boolean active,
        List<OptionAdminDto> options) {
}