package com.likelion.moneylog.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.moneylog.common.exception.ErrorCode;
import com.likelion.moneylog.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// 401 — 인증 필요(토큰 없음/위조/만료). 필터 단계라 @RestControllerAdvice가 못 잡으므로 여기서 직접 JSON을 써준다.
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        write(response, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }

    static void write(HttpServletResponse response, HttpStatus status, ErrorCode code) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiResponse<Void> body = ApiResponse.error(code.name(), code.getMessage());
        OBJECT_MAPPER.writeValue(response.getWriter(), body);
    }
}
