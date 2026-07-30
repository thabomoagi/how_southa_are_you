package com.thabo.how_sa_are_you.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank String username,

        @NotBlank String password

) {
}