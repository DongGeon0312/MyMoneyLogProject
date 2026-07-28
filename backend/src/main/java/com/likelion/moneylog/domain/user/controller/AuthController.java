package com.likelion.moneylog.domain.user.controller;

import com.likelion.moneylog.common.response.ApiResponse;
import com.likelion.moneylog.domain.user.dto.LoginRequest;
import com.likelion.moneylog.domain.user.dto.SignupRequest;
import com.likelion.moneylog.domain.user.dto.SignupResponse;
import com.likelion.moneylog.domain.user.dto.TokenResponse;
import com.likelion.moneylog.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest req) {
        return ApiResponse.success("회원가입에 성공했습니다.", authService.signup(req));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.success("로그인에 성공했습니다.", authService.login(req));
    }
}
