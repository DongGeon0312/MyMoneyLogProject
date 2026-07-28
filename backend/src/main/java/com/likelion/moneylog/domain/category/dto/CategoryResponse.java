package com.likelion.moneylog.domain.category.dto;

import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.category.entity.CategoryType;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType());
    }
}
