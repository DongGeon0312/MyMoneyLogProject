package com.likelion.moneylog.domain.category.controller;

import com.likelion.moneylog.common.response.ApiResponse;
import com.likelion.moneylog.domain.category.dto.CategoryRequest;
import com.likelion.moneylog.domain.category.dto.CategoryResponse;
import com.likelion.moneylog.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // TODO(1-8): userId를 @AuthenticationPrincipal 로그인 사용자로 교체
    private static final Long TEMP_USER_ID = 1L;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getList() {
        return ApiResponse.success("카테고리 목록을 조회했습니다.", categoryService.getMyCategories(TEMP_USER_ID));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest req) {
        CategoryResponse created = categoryService.create(TEMP_USER_ID, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("카테고리가 등록되었습니다.", created));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        return ApiResponse.success("카테고리가 수정되었습니다.", categoryService.update(TEMP_USER_ID, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(TEMP_USER_ID, id);
        return ResponseEntity.noContent().build();
    }
}
