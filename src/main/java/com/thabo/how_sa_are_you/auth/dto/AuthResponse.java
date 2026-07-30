package com.thabo.how_sa_are_you.auth.dto;

public record AuthResponse(

        String token,
        String username,
        String role

) {
}