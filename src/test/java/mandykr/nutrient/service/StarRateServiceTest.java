package mandykr.nutrient.service;

import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.repository.StarRateRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class StarRateServiceTest {

    @Autowired
    StarRateService starRateService;
    @Autowired
    StarRateRepository starRateRepository;
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

        assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("등록되지 않은 영양제로 별점을 조회하면 예외가 발생한다.")
    public void 영양제_조회_실패(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("testname");

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);
        //영양제 없는거로 조회하니까 에러 발생
        assertThat(starRateService.createStarRate(supplement.getId(),2,member1)).isEqualTo(EntityNotFoundException.class);
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

        assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(5.0);
    }

    @Test
    public void 별점_조회(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("testname");
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        assertThat(starRateService.getMemberStarRate(supplement1.getId(),member1).getId()).isNull();
    }

    @Test
    public void 별점_삭제(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("testname");
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        StarRate starRate = new StarRate(2,supplement1,member1);
        StarRate starRate1 = starRateRepository.save(starRate);

        assertThat(starRateRepository.findById(starRate1.getId()).isPresent()).isEqualTo(true);

        starRateService.deleteStarRate(starRate1.getId());

        assertThat(starRateRepository.findById(starRate1.getId()).isPresent()).isEqualTo(false);
    }
}