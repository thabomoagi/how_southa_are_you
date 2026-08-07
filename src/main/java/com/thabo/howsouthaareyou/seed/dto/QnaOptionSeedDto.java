package com.thabo.howsouthaareyou.seed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QnaOptionSeedDto(
        String text,
        @JsonProperty("isCorrect") Boolean correct) {
}