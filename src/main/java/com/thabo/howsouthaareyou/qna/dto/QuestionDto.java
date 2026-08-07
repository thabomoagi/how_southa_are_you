package com.thabo.howsouthaareyou.qna.dto;

import com.thabo.howsouthaareyou.qna.entity.Difficulty;

import java.util.List;

public record QuestionDto(
        Long id,
        Long categoryId,
        String prompt,
        Difficulty difficulty,
        List<OptionDto> options) {
}