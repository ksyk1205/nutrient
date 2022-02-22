package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
class SupplementStarRateRepositoryTest {
    @Autowired
    SupplementStarRateRepository supplementStarRateRepository;

    @Autowired
    SupplementRepository supplementRepository;

    @Autowired
    MemberRepository memberRepository;

    Supplement supplement;
    SupplementStarRate supplementStarRate;
    SupplementStarRate supplementStarRate2;
    Member member;
    Member member2;

    @BeforeEach
    void before(){
        supplement = supplementRepository.save(Supplement.builder().name("testSupplement").ranking(0.0).build()); ;

        member = Member.builder()
                .name("name1")
                .memberId("test").build();

        member = Member.builder()
                .name("name2")
                .memberId("test2").build();

        supplementStarRate = supplementStarRateRepository.save(new SupplementStarRate(3, supplement, member));;
        supplementStarRate2 = supplementStarRateRepository.save(new SupplementStarRate(2, supplement, member2));

        Member saveMember1 = memberRepository.save(member);
        Member saveMember2 = memberRepository.save(member2);

    }

    @Test
    void 별점리스트_조회(){
        //given

        //when
        List<SupplementStarRate> byStarRate = supplementStarRateRepository.findBySupplement(supplement);

        //then
        assertEquals(byStarRate.size(), 2);
    }
    @Test
    @DisplayName("영양제아이디랑 회원아이디로 별점 조회")
    void 별점_조회(){
        //given

        //when
        Optional<SupplementStarRate> bySupplementAndMember = supplementStarRateRepository.findBySupplementAndMember(supplement, member);
        //then
        assertEquals(bySupplementAndMember.get(), supplementStarRate);
    }

    @Test
    @DisplayName("회원아이디랑 별점아이디로 별점 조회")
    void 별점_조회1(){
        //given

        //when
        Optional<SupplementStarRate> byMemberAndId = supplementStarRateRepository.findByMemberAndId(member, supplementStarRate.getId());
        //then
        assertEquals(byMemberAndId.get(), supplementStarRate);
    }
}