package com.project.moviefilterbe.login.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users") // PostgreSQL에서 'user'는 예약어이므로 'users'로 이름을 지정합니다.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String profileImage;

    private String provider;   // google, kakao 등 구분
    private String providerId; // 소셜 서비스에서 주는 고유 번호

    // 유저의 권한 설정 (필요 시)
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        USER, ADMIN
    }
}