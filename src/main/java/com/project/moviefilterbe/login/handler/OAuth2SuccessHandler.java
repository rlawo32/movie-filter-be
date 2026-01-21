package com.project.moviefilterbe.login.handler;

import com.project.moviefilterbe.login.entity.AccountLog;
import com.project.moviefilterbe.login.entity.User;
import com.project.moviefilterbe.login.jwt.JwtTokenProvider;
import com.project.moviefilterbe.login.repository.AccountLogRepository;
import com.project.moviefilterbe.login.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AccountLogRepository accountLogRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = "";

        // 1. 구글 추출
        if (attributes.containsKey("email")) {
            email = (String) attributes.get("email");
        }
        // 2. 네이버 추출 (response 라는 키 안에 들어있음)
        else if (attributes.containsKey("response")) {
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
            email = (String) naverResponse.get("email");
        }
        // 3. 카카오 추출 (kakao_account 라는 키 안에 들어있음)
        // 카카오 추출 보강
        else if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            // profile이나 email이 있는지 꼼꼼히 체크
            if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
                email = (String) kakaoAccount.get("email");
            }
        }

        // 이메일이 없는 경우 방어 코드 (중요!)
        if (email == null || email.isEmpty()) {
            // 카카오 고유 ID(숫자)를 활용해 임시 이메일 형태를 만듦
            email = oAuth2User.getName() + "@kakao.com";
        }

        // 2. JWT 토큰 생성
        String token = tokenProvider.createToken(email, "USER");

        // account_log
        /* [작업자: ms] 1. DB에서 유저 정보를 가져오거나 생성 */
        final String finalEmail = email; // 람다용 상수
        User user = userRepository.findByEmail(finalEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        /* [작업자: ms] 2. 로그인 이력 저장 함수 호출 */
        saveAccountLog(user, request);

        // 3. 프론트엔드(Next.js) 리다이렉트 URL 생성
        // 예: http://localhost:3000/login-success?token=ey...
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login-success")
                .queryParam("token", token)
                .build().toUriString();

        // 4. 리다이렉트 실행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

    /* [작업자: ms] 로그인 로그 저장 로직 */
    private void saveAccountLog(User user, HttpServletRequest request) {
        String shotUUID = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String alId = "al_" + shotUUID;

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        AccountLog log = AccountLog.builder()
                .alId(alId)
                .user(user)  // 위에서 조회한 유저 객체 전달
                .alLoginIp(ip)
                .build();

        accountLogRepository.save(log);
    }
}