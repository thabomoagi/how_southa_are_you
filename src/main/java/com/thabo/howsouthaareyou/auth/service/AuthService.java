package com.thabo.howsouthaareyou.auth.service;

import com.thabo.howsouthaareyou.auth.dto.AuthResponse;
import com.thabo.howsouthaareyou.auth.dto.LoginRequest;
import com.thabo.howsouthaareyou.auth.dto.RefreshTokenRequest;
import com.thabo.howsouthaareyou.auth.dto.RegisterRequest;
import com.thabo.howsouthaareyou.auth.entity.RefreshToken;
import com.thabo.howsouthaareyou.auth.repository.RefreshTokenRepository;
import com.thabo.howsouthaareyou.auth.security.JwtService;
import com.thabo.howsouthaareyou.common.exception.ConflictException;
import com.thabo.howsouthaareyou.common.exception.UnauthorizedException;
import com.thabo.howsouthaareyou.user.entity.User;
import com.thabo.howsouthaareyou.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = findUserByIdentifier(request.getIdentifier())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtService.isRefreshTokenValid(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not recognized"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = storedToken.getUser();

        refreshTokenRepository.delete(storedToken);

        return buildAuthResponse(user);
    }

    public void logout(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(refreshTokenRepository::delete);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePictureUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveRefreshToken(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationMs() / 1000))
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(token);
    }

    private Optional<User> findUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier);
        }

        return userRepository.findByUsername(identifier);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("Unable to hash refresh token", exception);
        }
    }
}