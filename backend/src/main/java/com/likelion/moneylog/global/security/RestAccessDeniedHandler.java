package com.likelion.moneylog.global.security;

import com.likelion.moneylog.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

// 403 — 시큐리티 필터 단계에서의 인가 실패 (유효 토큰이지만 접근 권한 없음)
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        RestAuthenticationEntryPoint.write(response, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN);
    }
}
