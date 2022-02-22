package mandykr.nutrient.service.member;

import mandykr.nutrient.dto.member.MemberRequest;
import mandykr.nutrient.dto.member.MemberResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService{

    void createMember(MemberRequest memberRequest);

    MemberResponse getByMemberId(String memberId);

    MemberResponse getByMemberIdAndPassword(String memberId, String password);
}
