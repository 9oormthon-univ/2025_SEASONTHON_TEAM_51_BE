package com.mockit.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.mockit.domain.model.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    // ✅ 카카오 로그인 공급자
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    // ✅ 카카오에서 내려주는 id (subject)
    @Column(nullable = false, length = 64)
    private String subject;

    // ✅ 사용자 닉네임
    @Column(length = 100)
    private String nickname;

    // ✅ 프로필 이미지
    private String profileImageUrl;

    public enum Provider {
        KAKAO
    }

    // ✅ 새 카카오 유저 생성
    public static Member fromKakao(String subject, String nickname, String profileImageUrl) {
        Member member = new Member();
        member.setProvider(Provider.KAKAO);
        member.setSubject(subject);
        member.setNickname(nickname);
        member.setProfileImageUrl(profileImageUrl);
        return member;
    }

    // ✅ 기존 카카오 유저 업데이트
    public void updateFromKakao(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}