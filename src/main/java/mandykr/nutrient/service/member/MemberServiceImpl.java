package mandykr.nutrient.service.member;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.member.MemberRequest;
import mandykr.nutrient.dto.member.MemberResponse;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.member.Role;
import mandykr.nutrient.repository.member.MemberRepository;
import mandykr.nutrient.security.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public MemberResponse getByMemberId(String memberId) {
        Member member = findByMemberIdCatchException(memberId);
        return new MemberResponse(member);
    }

    @Override
    public MemberResponse getByMemberIdAndPassword(String memberId, String password) {
        Member member = findByMemberIdCatchException(memberId);
        if(passwordEncoder.matches(password, member.getPassword())){
            return new MemberResponse(member, password);
        }else{
            throw new IllegalArgumentException("Password Not Coincide");
        }
    }


    @Override
    public void createMember(MemberRequest memberRequest) {
        if (findByMemberId(memberRequest.getMemberId()).isPresent()) {
            throw new IllegalArgumentException("memberId that exists.");
        }
        Member member = Member.builder()
                .memberId(memberRequest.getMemberId())
                .name(memberRequest.getName())
                .roles(memberRequest.getRoles())
                .password(passwordEncoder.encode(memberRequest.getPassword()))
                .build();
        memberRepository.save(member);
    }

    private Optional<Member> findByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId);
    }


    private Member findByMemberIdCatchException(String memberId) {
        return memberRepository.findByMemberId(memberId).orElseThrow(()->new IllegalArgumentException("member does not have."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByMemberId(username).orElseThrow(() -> new UsernameNotFoundException("member not found"));
    }
}
