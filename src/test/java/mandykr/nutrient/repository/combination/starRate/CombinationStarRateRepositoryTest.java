package mandykr.nutrient.repository.combination.starRate;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.repository.combination.CombinationRepository;
import mandykr.nutrient.repository.combination.starrate.CombinationStarRateRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfig.class)
class CombinationStarRateRepositoryTest {
    @Autowired
    CombinationStarRateRepository combinationStarRateRepository;
    @Autowired
    CombinationRepository combinationRepository;

    @Autowired
    MemberRepository memberRepository;


    Combination combination;
    Member member;


    @BeforeEach
    public void setup(){
        this.combination = combinationRepository.save(Combination.builder()
                .caption("영양제조합")
                .rating(0.0)
                .build());
        Member member = new Member();
        member.setMemberId("testMember");
        member.setName("KIM");
        this.member = memberRepository.save(member);
    }

    @Test
    public void 등록(){
        CombinationStarRate starRate = combinationStarRateRepository.save(
                                        CombinationStarRate.builder()
                                        .starNumber(2)
                                        .member(member)
                                        .combination(combination)
                                        .build());
        Assertions.assertThat(starRate).isEqualTo(combinationStarRateRepository.findById(starRate.getId()).get());
    }


    @Test
    public void 업데이트(){
        CombinationStarRate starRate = combinationStarRateRepository.save(
                CombinationStarRate.builder()
                        .starNumber(2)
                        .member(member)
                        .combination(combination)
                        .build());
        starRate.updateStarNumber(5);
        Assertions.assertThat(starRate).isEqualTo(combinationStarRateRepository.findById(starRate.getId()).get());
        Assertions.assertThat(starRate.getStarNumber())
                .isEqualTo(combinationStarRateRepository.findById(starRate.getId()).get().getStarNumber());
    }

    @Test
    public void 조회_영양제ID_MEMBER(){
        combinationStarRateRepository.save(
                CombinationStarRate.builder()
                        .starNumber(2)
                        .member(member)
                        .combination(combination)
                        .build());

        assertTrue(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(),member).isPresent());
    }

    @Test
    public void 조회_영양제ID_MEMBER_없을때(){
        assertFalse(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(),member).isPresent());
    }

    @Test
    public void 조회_영양제ID_MEMBER_ID(){
        CombinationStarRate combinationStarRate = combinationStarRateRepository.save(
                CombinationStarRate.builder()
                        .starNumber(2)
                        .member(member)
                        .combination(combination)
                        .build());
        assertTrue(combinationStarRateRepository.findIdAndMemberAndComb(combinationStarRate.getId(), member, combination.getId()).isPresent());
    }

    @Test
    public void 조회_영양제ID_MEMBER_ID_없을때(){
        assertFalse(combinationStarRateRepository.findIdAndMemberAndComb(0L, member, combination.getId()).isPresent());
    }

    @Test
    public void 조회_MEMBER_ID(){
        CombinationStarRate combinationStarRate = combinationStarRateRepository.save(
                CombinationStarRate.builder()
                        .starNumber(2)
                        .member(member)
                        .combination(combination)
                        .build());
        assertTrue(combinationStarRateRepository.findIdAndMember(combinationStarRate.getId(), member).isPresent());
    }

    @Test
    public void 조회_MEMBER_ID_없을때(){
        assertFalse(combinationStarRateRepository.findIdAndMember(0L, member).isPresent());
    }
}
