package mandykr.nutrient.repository.combination.starRate;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CombinationStarRateRepositoryTest {
    @Autowired
    CombinationStarRateRepository combinationStarRateRepository;
    @Autowired
    CombinationRepository combinationRepository;

    @Autowired
    MemberRepository memberRepository;


    private Combination combination;
    private Member member;


    @BeforeEach
    public void 등록전_값_입력(){
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
    public void 등록_테스트(){
        CombinationStarRate starRate = combinationStarRateRepository.save(
                                        CombinationStarRate.builder()
                                        .starNumber(2)
                                        .member(member)
                                        .combination(combination)
                                        .build());
        Assertions.assertThat(starRate).isEqualTo(combinationStarRateRepository.findById(starRate.getId()).get());
    }


    @Test
    public void 업데이트_테스트(){
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
    public void 영양제번호로_조회_테스트(){
        CombinationStarRate starRate1 = combinationStarRateRepository.save(
                CombinationStarRate.builder()
                        .starNumber(2)
                        .member(member)
                        .combination(combination)
                        .build());

        assertTrue(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(),member).isPresent());
    }

    @Test
    public void 영양제번호로_조회_테스트_없을때(){
        assertFalse(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(),member).isPresent());
    }

}
