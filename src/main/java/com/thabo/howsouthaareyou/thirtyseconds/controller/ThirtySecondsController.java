package com.thabo.howsouthaareyou.thirtyseconds.controller;

import com.thabo.howsouthaareyou.common.dto.ApiResponse;
import com.thabo.howsouthaareyou.thirtyseconds.dto.GameResultResponse;
import com.thabo.howsouthaareyou.thirtyseconds.dto.GameSummaryResponse;
import com.thabo.howsouthaareyou.thirtyseconds.dto.StartGameRequest;
import com.thabo.howsouthaareyou.thirtyseconds.dto.StartGameResponse;
import com.thabo.howsouthaareyou.thirtyseconds.dto.SubmitRoundScoreRequest;
import com.thabo.howsouthaareyou.thirtyseconds.dto.SubmitRoundScoreResponse;
import com.thabo.howsouthaareyou.thirtyseconds.service.ThirtySecondsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/thirty-seconds/games")
@RequiredArgsConstructor
public class ThirtySecondsController {

    private final ThirtySecondsService thirtySecondsService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StartGameResponse>> startGame(
            @Valid @RequestBody StartGameRequest request) {
        StartGameResponse response = thirtySecondsService.startGame(request);

        return ResponseEntity.ok(
                ApiResponse.success("Game started", response));
    }

    @PostMapping("/{gameId}/rounds/score")
    public ResponseEntity<ApiResponse<SubmitRoundScoreResponse>> submitRoundScore(
            @PathVariable UUID gameId,
            @Valid @RequestBody SubmitRoundScoreRequest request) {
        SubmitRoundScoreResponse response = thirtySecondsService.submitRoundScore(gameId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Round score submitted", response));
    }

    @PostMapping("/{gameId}/complete")
    public ResponseEntity<ApiResponse<GameResultResponse>> completeGame(
            @PathVariable UUID gameId) {
        GameResultResponse response = thirtySecondsService.completeGame(gameId);

        return ResponseEntity.ok(
                ApiResponse.success("Game completed", response));
    }

    @GetMapping("/{gameId}/result")
    public ResponseEntity<ApiResponse<GameResultResponse>> getGameResult(
            @PathVariable UUID gameId) {
        GameResultResponse response = thirtySecondsService.getGameResult(gameId);

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<GameSummaryResponse>>> getGameHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("startedAt").descending());

        Page<GameSummaryResponse> response = thirtySecondsService.getGameHistory(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }
}