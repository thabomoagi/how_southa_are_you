package com.thabo.how_sa_are_you.qna.controller;

import com.thabo.how_sa_are_you.qna.dto.CreateQuestionRequest;
import com.thabo.how_sa_are_you.qna.dto.QuestionAdminDto;
import com.thabo.how_sa_are_you.qna.service.QuestionAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/qna/questions")
@PreAuthorize("hasRole('ADMIN')")
public class QuestionAdminController {

    private final QuestionAdminService questionAdminService;

    public QuestionAdminController(QuestionAdminService questionAdminService) {
        this.questionAdminService = questionAdminService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionAdminDto>> listAll() {
        return ResponseEntity.ok(questionAdminService.listAll());
    }

    @PostMapping
    public ResponseEntity<QuestionAdminDto> create(@Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(questionAdminService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionAdminDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(questionAdminService.update(id, request));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        questionAdminService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable UUID id) {
        questionAdminService.reactivate(id);
        return ResponseEntity.noContent().build();
    }
}