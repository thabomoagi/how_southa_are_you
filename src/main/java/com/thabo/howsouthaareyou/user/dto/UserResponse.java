package com.thabo.howsouthaareyou.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String profilePictureUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}