package mandykr.nutrient.service;

import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;


@SpringBootTest
@Transactional
class StarRateServiceTest {

    @Autowired
    StarRateService starRateService;
    @Autowired
    SupplementRepository supplementRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 별점_등록_성공(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("testname");
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        StarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2,member1);

        Assertions.assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(2.0);
    }

    @Test
    public void 별점_조회_실패(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("testname");

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        starRateService.createStarRate(supplement.getId(),2,member1); //영양제 없는거로 조회하니까 에러 발생

    }

    @Test
    public void 별점_수정_성공(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("testname");
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        StarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2,member1);
        starRateService.updateStarRate(supplement1.getId(),starRateDto.getId(),5,member1);

        Assertions.assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(5.0);
    }

}