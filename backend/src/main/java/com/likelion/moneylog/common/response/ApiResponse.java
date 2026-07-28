package com.likelion.moneylog.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

// 모든 REST 응답을 감싸는 공통 응답 래퍼 (docs/api-spec.md 4절 공통 응답 규약)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Object meta, // 목록 조회 시 PageMeta 등을 담음 (없으면 미출력)
        String code  // 실패 시 기계용 에러 코드 (1-8 전역 예외에서 채움)
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청에 성공했습니다.", data, null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data, Object meta) {
        return new ApiResponse<>(true, message, data, meta, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, message, null, null, code);
    }
}
