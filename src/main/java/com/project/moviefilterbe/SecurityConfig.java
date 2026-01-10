package com.project.moviefilterbe;

import com.project.moviefilterbe.login.service.CustomOAuth2UserService; // 추가
import lombok.RequiredArgsConstructor; // 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // 생성자 주입을 위해 추가
public class SecurityConfig {

    // 우리가 만든 서비스를 가져옵니다.
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    // SecurityConfig.java의 filterChain 메소드 수정 - ms 20260110
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 성공 시 리다이렉트 경로를 프론트엔드 주소로 명시
                        .defaultSuccessUrl("http://localhost:3000", true)
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}