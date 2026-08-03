package com.thabo.how_sa_are_you.qna.controller;

import com.thabo.how_sa_are_you.qna.dto.AttemptResultResponse;
import com.thabo.how_sa_are_you.qna.dto.StartAttemptResponse;
import com.thabo.how_sa_are_you.qna.dto.SubmitAttemptRequest;
import com.thabo.how_sa_are_you.qna.service.GameService;
import com.thabo.how_sa_are_you.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/qna/attempts")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public ResponseEntity<StartAttemptResponse> start(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(gameService.startAttempt(user));
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<AttemptResultResponse> submit(
            @AuthenticationPrincipal User user,
            @PathVariable UUID attemptId,
            @Valid @RequestBody SubmitAttemptRequest request) {
        return ResponseEntity.ok(gameService.submitAttempt(user, attemptId, request));
    }
}