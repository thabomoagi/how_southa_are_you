package com.thabo.howsouthaareyou.qna.controller;

import com.thabo.howsouthaareyou.common.dto.ApiResponse;
import com.thabo.howsouthaareyou.qna.dto.CategoryResponse;
import com.thabo.howsouthaareyou.qna.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qna/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(
                ApiResponse.success(categoryService.getAllCategories()));
    }
}