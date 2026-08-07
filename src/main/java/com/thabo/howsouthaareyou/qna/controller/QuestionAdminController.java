package com.thabo.howsouthaareyou.qna.controller;

import com.thabo.howsouthaareyou.common.dto.ApiResponse;
import com.thabo.howsouthaareyou.qna.dto.CreateQuestionRequest;
import com.thabo.howsouthaareyou.qna.dto.QuestionAdminDto;
import com.thabo.howsouthaareyou.qna.service.QuestionAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/qna/questions")
@RequiredArgsConstructor
public class QuestionAdminController {

    private final QuestionAdminService questionAdminService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionAdminDto>> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request) {
        QuestionAdminDto response = questionAdminService.createQuestion(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionAdminDto>>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending());

        Page<QuestionAdminDto> response = questionAdminService.getQuestions(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionAdminDto>> getQuestion(
            @PathVariable Long questionId) {
        QuestionAdminDto response = questionAdminService.getQuestion(questionId);

        return ResponseEntity.ok(
                ApiResponse.success(response));
    }

    @PatchMapping("/{questionId}/activate")
    public ResponseEntity<ApiResponse<QuestionAdminDto>> activateQuestion(
            @PathVariable Long questionId) {
        QuestionAdminDto response = questionAdminService.activateQuestion(questionId);

        return ResponseEntity.ok(
                ApiResponse.success("Question activated", response));
    }

    @PatchMapping("/{questionId}/deactivate")
    public ResponseEntity<ApiResponse<QuestionAdminDto>> deactivateQuestion(
            @PathVariable Long questionId) {
        QuestionAdminDto response = questionAdminService.deactivateQuestion(questionId);

        return ResponseEntity.ok(
                ApiResponse.success("Question deactivated", response));
    }
}