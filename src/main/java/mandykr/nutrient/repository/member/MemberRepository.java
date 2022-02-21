package mandykr.nutrient.repository.member;

import mandykr.nutrient.dto.member.MemberResponse;
import mandykr.nutrient.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,String> {
    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByMemberIdAndPassword(String memberId, String password);
}
