package com.thabo.howsouthaareyou.thirtyseconds.dto;

public record RoundDto(
        Long roundId,
        Integer roundNumber,
        String playerName,
        String prompt,
        Integer score) {
}