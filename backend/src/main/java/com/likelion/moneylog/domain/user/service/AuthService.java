package com.likelion.moneylog.domain.user.service;

import com.likelion.moneylog.common.exception.CustomException;
import com.likelion.moneylog.common.exception.ErrorCode;
import com.likelion.moneylog.domain.category.service.CategoryService;
import com.likelion.moneylog.domain.user.dto.LoginRequest;
import com.likelion.moneylog.domain.user.dto.SignupRequest;
import com.likelion.moneylog.domain.user.dto.SignupResponse;
import com.likelion.moneylog.domain.user.dto.TokenResponse;
import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import com.likelion.moneylog.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryService categoryService; // 기본 카테고리 시드용
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password())) // 평문 저장 금지
                .nickname(req.nickname())
                .build();
        userRepository.save(user);

        // 회원가입 시 기본 카테고리 자동 생성 (식비/교통/주거/문화, 급여/용돈)
        categoryService.seedDefaultCategories(user);

        return SignupResponse.from(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 이메일 존재 여부와 무관하게 동일한 메시지로 응답 (계정 열거 방지)
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtProvider.createToken(user.getId(), user.getEmail());
        return TokenResponse.of(token, user.getNickname());
    }
}
