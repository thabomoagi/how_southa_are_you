package com.thabo.howsouthaareyou.user.service;

import com.thabo.howsouthaareyou.common.exception.ConflictException;
import com.thabo.howsouthaareyou.qna.repository.AttemptRepository;
import com.thabo.howsouthaareyou.thirtyseconds.repository.ThirtySecondsGameRepository;
import com.thabo.howsouthaareyou.user.dto.UpdateUserRequest;
import com.thabo.howsouthaareyou.user.dto.UserResponse;
import com.thabo.howsouthaareyou.user.dto.UserStatsResponse;
import com.thabo.howsouthaareyou.user.entity.User;
import com.thabo.howsouthaareyou.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AttemptRepository attemptRepository;
    private final ThirtySecondsGameRepository thirtySecondsGameRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = currentUserProvider.getCurrentUser();

        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        User user = currentUserProvider.getCurrentUser();

        if (request.username() != null && !request.username().isBlank()) {
            String username = request.username().trim();

            if (!username.equals(user.getUsername()) && userRepository.existsByUsername(username)) {
                throw new ConflictException("Username is already taken");
            }

            user.setUsername(username);
        }

        if (request.email() != null && !request.email().isBlank()) {
            String email = request.email().trim().toLowerCase();

            if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new ConflictException("Email is already in use");
            }

            user.setEmail(email);
        }

        if (request.profilePictureUrl() != null && !request.profilePictureUrl().isBlank()) {
            user.setProfilePictureUrl(request.profilePictureUrl().trim());
        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats() {
        User user = currentUserProvider.getCurrentUser();

        long totalQnaAttempts = attemptRepository.countByUserId(user.getId());
        Integer qnaBestScore = attemptRepository.findBestScoreByUserId(user.getId());
        Double qnaAverageScore = attemptRepository.findAverageScoreByUserId(user.getId());

        long totalThirtySecondsGames = thirtySecondsGameRepository.countByUserId(user.getId());
        Integer thirtySecondsBestScore = thirtySecondsGameRepository.findBestScoreByUserId(user.getId());

        long totalGamesPlayed = totalQnaAttempts + totalThirtySecondsGames;

        return new UserStatsResponse(
                totalQnaAttempts,
                qnaBestScore,
                qnaAverageScore,
                totalThirtySecondsGames,
                thirtySecondsBestScore,
                totalGamesPlayed);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePictureUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}