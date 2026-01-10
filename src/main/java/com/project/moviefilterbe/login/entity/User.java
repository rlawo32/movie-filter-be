package com.project.moviefilterbe.login.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "mf_users_info") // PostgreSQL에서 'user'는 예약어이므로 'users'로 이름을 지정합니다.
public class User {

    @Id
    @Column(name = "ui_id") // 2. DB 컬럼명 ui_id와 매핑
    private String uiId;

    @Column(name = "ui_name", nullable = false)
    private String name;

    @Column(name = "ui_image")
    private String profileImage;

    // Supabase에 email 컬럼을 추가하지 않았다면 에러가 날 수 있습니다.
    @Column(name = "ui_email")
    private String email;

    @Column(name = "ui_role")
    private String role;

    @Column(name = "ui_status")
    private String status;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;
}