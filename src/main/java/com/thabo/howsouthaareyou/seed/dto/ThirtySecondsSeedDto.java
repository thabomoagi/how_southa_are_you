package com.thabo.howsouthaareyou.seed.dto;

import java.util.List;

public record ThirtySecondsSeedDto(
        String externalId,
        String difficulty,
        List<String> words) {
}