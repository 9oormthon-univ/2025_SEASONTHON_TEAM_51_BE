package com.mockit.domain.user.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class UserAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String provider; // 'DEV' | 'KAKAO' 등

    @Column(length = 64)
    private String subject;  // 소셜 고유 id (DEV 환경에선 임의값)

    @Column(length = 100)
    private String nickname;

    public UserAccount(String provider, String subject, String nickname) {
        this.provider = provider;
        this.subject = subject;
        this.nickname = nickname;
    }
}
