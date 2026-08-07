package com.thabo.howsouthaareyou.seed.dto;

import java.util.List;

public record QnaSeedDto(
        String externalId,
        String category,
        String difficulty,
        String era,
        String question,
        List<QnaOptionSeedDto> options,
        String explanation) {
}