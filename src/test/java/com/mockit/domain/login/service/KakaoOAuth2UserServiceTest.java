package com.mockit.domain.login.service;

import com.mockit.domain.member.domain.entity.Member;
import com.mockit.domain.member.domain.entity.Member.Provider;
import com.mockit.domain.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KakaoOAuth2UserServiceTest {

    @Test
    void 카카오응답_파싱_upsert_memberId_attr_세팅() {
        var repo = mock(MemberRepository.class);
        var svc = spy(new KakaoOAuth2UserService(repo));

        Map<String,Object> attrs = new HashMap<>();
        attrs.put("id", 123456789L);
        Map<String,Object> profile = Map.of("nickname","테스터","profile_image_url","http://img");
        Map<String,Object> account = new HashMap<>();
        account.put("email","tester@kakao.com");
        account.put("profile", profile);
        attrs.put("kakao_account", account);

        doReturn(new OAuth2User() {
            @Override public Map<String, Object> getAttributes() { return attrs; }
            @Override public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities(){ return List.of(); }
            @Override public String getName() { return "123456789"; }
        }).when(svc).loadUser(any(OAuth2UserRequest.class));

        when(repo.findByProviderAndSubject(Provider.KAKAO, "123456789")).thenReturn(Optional.empty());
        when(repo.save(any(Member.class))).thenAnswer(inv -> { Member m = inv.getArgument(0); m.setMemberId(1L); return m; });

        var req = new OAuth2UserRequest(
                null,
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,"t", Instant.now(), Instant.now().plusSeconds(3600))
        );

        OAuth2User user = svc.loadUser(req);

        var captor = ArgumentCaptor.forClass(Member.class);
        verify(repo).save(captor.capture());
        Member saved = captor.getValue();

        assertThat(saved.getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(saved.getSubject()).isEqualTo("123456789");
        assertThat(saved.getEmail()).isEqualTo("tester@kakao.com");
        assertThat(saved.getNickname()).isEqualTo("테스터");
        assertThat(user.getAttributes()).containsKey("memberId");
    }
}