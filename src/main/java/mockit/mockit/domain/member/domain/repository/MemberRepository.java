package mockit.mockit.domain.member.domain.repository;

import mockit.mockit.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
