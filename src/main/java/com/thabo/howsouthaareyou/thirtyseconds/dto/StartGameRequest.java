package com.thabo.howsouthaareyou.thirtyseconds.dto;

import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StartGameRequest(
        @NotNull ThirtySecondsMode mode,
        List<String> playerNames,
        @Min(1) @Max(10) Integer roundsPerPlayer) {
}