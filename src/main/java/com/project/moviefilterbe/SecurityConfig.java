package com.project.moviefilterbe;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보안 비활성화 (API 테스트 시 필수)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청에 대해 로그인 없이 허용
                )
                .formLogin(form -> form.disable()) // 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()); // 기본 인증 비활성화

        return http.build();
    }
}