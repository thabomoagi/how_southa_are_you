package com.thabo.howsouthaareyou.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 50) String username,

        @Email String email,

        @Size(max = 500) String profilePictureUrl) {
}