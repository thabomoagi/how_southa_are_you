package com.thabo.howsouthaareyou.user.controller;

import com.thabo.howsouthaareyou.common.dto.ApiResponse;
import com.thabo.howsouthaareyou.user.dto.UpdateUserRequest;
import com.thabo.howsouthaareyou.user.dto.UserResponse;
import com.thabo.howsouthaareyou.user.dto.UserStatsResponse;
import com.thabo.howsouthaareyou.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse response = userService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateCurrentUser(request);

        return ResponseEntity.ok(
                ApiResponse.success("Profile updated", response));
    }

    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats() {
        UserStatsResponse response = userService.getUserStats();

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }
}