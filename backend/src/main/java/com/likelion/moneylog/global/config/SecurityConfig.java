package com.likelion.moneylog.global.config;

import com.likelion.moneylog.global.security.JwtAuthenticationFilter;
import com.likelion.moneylog.global.security.RestAccessDeniedHandler;
import com.likelion.moneylog.global.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    // 인증 없이 접근 허용할 경로 (회원가입/로그인/Swagger/H2 콘솔-로컬 전용/정적 프론트 화면)
    private static final String[] WHITELIST = {
            "/api/auth/signup",
            "/api/auth/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/", "/index.html", "/login.html", "/transactions.html", "/statistics.html", "/api.js"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT는 헤더로 오므로 CSRF 불필요
                .csrf(csrf -> csrf.disable())
                // H2 콘솔이 iframe을 쓰므로 로컬 개발 편의상 프레임 차단을 완화
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // 세션을 만들지 않는 무상태 방식
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST).permitAll()
                        .anyRequest().authenticated())
                // 시큐리티가 던지는 인증/인가 예외도 공통 응답 형식으로 내려준다
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                // JWT 필터를 기본 인증 필터 앞에 끼운다
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
