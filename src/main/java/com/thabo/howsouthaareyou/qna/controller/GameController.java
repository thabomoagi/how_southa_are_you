package com.thabo.howsouthaareyou.qna.controller;

import com.thabo.howsouthaareyou.common.dto.ApiResponse;
import com.thabo.howsouthaareyou.qna.dto.AttemptResultResponse;
import com.thabo.howsouthaareyou.qna.dto.AttemptSummaryResponse;
import com.thabo.howsouthaareyou.qna.dto.StartAttemptRequest;
import com.thabo.howsouthaareyou.qna.dto.StartAttemptResponse;
import com.thabo.howsouthaareyou.qna.dto.SubmitAttemptRequest;
import com.thabo.howsouthaareyou.qna.service.AttemptHistoryService;
import com.thabo.howsouthaareyou.qna.service.GameService;
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
@RequestMapping("/api/v1/qna/attempts")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final AttemptHistoryService attemptHistoryService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StartAttemptResponse>> startAttempt(
            @Valid @RequestBody StartAttemptRequest request) {
        StartAttemptResponse response = gameService.startAttempt(request);

        return ResponseEntity.ok(
                ApiResponse.success("Attempt started", response));
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ApiResponse<AttemptResultResponse>> submitAttempt(
            @PathVariable UUID attemptId,
            @Valid @RequestBody SubmitAttemptRequest request) {
        AttemptResultResponse response = gameService.submitAttempt(attemptId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Attempt submitted", response));
    }

    @GetMapping("/{attemptId}/result")
    public ResponseEntity<ApiResponse<AttemptResultResponse>> getAttemptResult(
            @PathVariable UUID attemptId) {
        AttemptResultResponse response = gameService.getAttemptResult(attemptId);

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<AttemptSummaryResponse>>> getAttemptHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("startedAt").descending());

        Page<AttemptSummaryResponse> response = attemptHistoryService.getAttemptHistory(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }
}