package com.likelion.moneylog.common.response;

import org.springframework.data.domain.Page;

// 목록 응답의 meta.pagination 형태: { pagination: { page, size, totalItems, totalPages, hasNext, hasPrev } }
public record PageMeta(Pagination pagination) {

    public record Pagination(
            int page,
            int size,
            long totalItems,
            int totalPages,
            boolean hasNext,
            boolean hasPrev
    ) {}

    public static PageMeta from(Page<?> page) {
        return new PageMeta(new Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        ));
    }
}
