package com.thabo.howsouthaareyou.qna.controller;

import com.thabo.howsouthaareyou.common.dto.ApiResponse;
import com.thabo.howsouthaareyou.qna.dto.LeaderboardResponse;
import com.thabo.howsouthaareyou.qna.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leaderboard/qna")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<LeaderboardResponse>> getLeaderboard(
            @RequestParam(defaultValue = "ALL") String period,
            @RequestParam(defaultValue = "20") int limit) {
        LeaderboardResponse response = leaderboardService.getLeaderboard(period, limit);

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }
}