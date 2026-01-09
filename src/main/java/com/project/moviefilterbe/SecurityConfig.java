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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 메인 페이지나 로그인 관련 주소는 모두 허용
                        .requestMatchers("/", "/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 소셜 로그인 설정 추가
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 핵심: 작성한 서비스 연결
                        )
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}