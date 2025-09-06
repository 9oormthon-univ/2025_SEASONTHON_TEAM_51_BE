package com.mockit.domain.member.domain.repository;

import com.mockit.domain.member.domain.entity.Member;
import com.mockit.domain.member.domain.entity.Member.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndSubject(Provider provider, String subject);
}