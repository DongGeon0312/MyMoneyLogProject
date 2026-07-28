package com.likelion.moneylog.common.exception;

// 도메인 조회 실패 시 사용하는 임시 예외.
// 1-8에서 CustomException + ErrorCode + @RestControllerAdvice로 대체/확장될 예정.
public class NotFoundException extends RuntimeException {

    private final String code;

    public NotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
