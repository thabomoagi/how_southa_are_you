package com.thabo.howsouthaareyou.thirtyseconds.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SubmitRoundScoreRequest(
        @NotNull Long roundId,
        @NotNull @Min(0) @Max(1000) Integer score) {
}