package mandykr.nutrient.service.Supplement;

import mandykr.nutrient.dto.supplement.SupplementStarRateDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.repository.supplement.SupplementStarRateRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;

import mandykr.nutrient.service.supplement.SupplementStarRateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class SupplementStarRateServiceTest {

    @Autowired
    SupplementStarRateService starRateService;
    @Autowired
    SupplementStarRateRepository starRateRepository;
    @Autowired
    SupplementRepository supplementRepository;
    @Autowired
    MemberRepository memberRepository;


    @Test
    public void 별점_등록_성공(){
        Supplement supplement = Supplement.builder().name("testname").ranking(0.0).build();
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        SupplementStarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2,member1);

        assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("등록되지 않은 영양제로 별점을 조회하면 예외가 발생한다.")
    public void 영양제_조회_실패(){
        Supplement supplement = Supplement.builder().name("testname").ranking(0.0).build();

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);
        //영양제 없는거로 조회하니까 에러 발생
        assertThrows(InvalidDataAccessApiUsageException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                starRateService.createStarRate(supplement.getId(),2,member1);
            }
        });
    }

    @Test
    public void 별점_수정_성공(){
        Supplement supplement = Supplement.builder().name("testname").ranking(0.0).build();
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        SupplementStarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2,member1);


        starRateService.updateStarRate(supplement1.getId(),starRateDto.getId(),5,member1);

        assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("별점 수정을 위하여 별점 조회 했을때 빈 객체가 반환되는지 확인한다.")
    public void 별점_조회(){
        Supplement supplement = Supplement.builder().name("testname").ranking(0.0).build();
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        assertThat(starRateService.getStarRateWithMember(supplement1.getId(),member1).getId()).isNull();
    }

    @Test
    public void 별점_삭제(){
        Supplement supplement = Supplement.builder().name("testname").ranking(0.0).build();
        Supplement supplement1 = supplementRepository.save(supplement);

        Member member = new Member();
        member.setMemberId("test");
        member.setName("name1");
        Member member1 = memberRepository.save(member);

        SupplementStarRate starRate = new SupplementStarRate(2,supplement1,member1);
        SupplementStarRate starRate1 = starRateRepository.save(starRate);

        assertThat(starRateRepository.findById(starRate1.getId()).isPresent()).isEqualTo(true);

        starRateService.deleteStarRate(starRate1.getId());

        assertThat(starRateRepository.findById(starRate1.getId()).isPresent()).isEqualTo(false);
    }
}