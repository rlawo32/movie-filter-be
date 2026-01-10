package com.project.moviefilterbe.login.service;

import com.project.moviefilterbe.login.entity.User;
import com.project.moviefilterbe.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 구글에서 받은 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String providerId = oAuth2User.getName();

        // [임시 테스트 로직] 로그 확인
        System.out.println("======= 소셜 로그인 정보 =======");
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Provider ID: " + providerId);
        System.out.println("===============================");

        // DB 저장 로직 주석
        // CustomOAuth2UserService.java 내부
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .uiId(email) // PK인 ui_id에 값 할당
                                .email(email)
                                .name(name)
                                .profileImage(picture)
                                .role("USER")   // 기본값 설정
                                .status("ACTIVE") // 기본값 설정
                                .provider("google")
                                .providerId(providerId)
                                .build()
                ));


        return oAuth2User;
    }
}