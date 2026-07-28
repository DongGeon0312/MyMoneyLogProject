package com.likelion.moneylog.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 임시 설정: 2일차(카테고리/거래 CRUD) 동안 모든 요청을 허용해 개발/테스트를 막지 않는다.
// 1-8에서 JWT 인증 + "본인 데이터만" 인가 로직으로 전면 교체 예정.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
