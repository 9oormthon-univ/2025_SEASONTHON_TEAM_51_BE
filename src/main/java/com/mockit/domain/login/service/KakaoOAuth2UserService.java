package com.mockit.domain.login.service;

import com.mockit.domain.member.domain.entity.Member;
import com.mockit.domain.member.domain.entity.Member.Provider;
import com.mockit.domain.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(req);
        Map<String, Object> attr = new HashMap<>(oAuth2User.getAttributes());

        // ✅ 전체 attributes 로그 찍기
        System.out.println("Kakao attributes: " + attr);

        String kakaoId = String.valueOf(attr.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attr.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;

        String nickname = profile != null ? (String) profile.get("nickname") : null;
        String profileImageUrl = profile != null ? (String) profile.get("profile_image_url") : null;

        Member member = memberRepository.findByProviderAndSubject(Provider.KAKAO, kakaoId)
                .map(m -> { m.updateFromKakao(nickname, profileImageUrl); return m; })
                .orElseGet(() -> memberRepository.save(Member.fromKakao(kakaoId, nickname, profileImageUrl)));

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        attr.put("memberId", member.getMemberId());

        return new DefaultOAuth2User(authorities, attr, "id");
    }
}