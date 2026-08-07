package com.thabo.howsouthaareyou.qna.dto;

public record OptionAdminDto(
        Long id,
        String optionText,
        Boolean correct) {
}