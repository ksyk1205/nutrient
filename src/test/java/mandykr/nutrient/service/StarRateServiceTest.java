package mandykr.nutrient.service;

import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
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

    @Test
    public void 별점_등록_성공(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("test");
        Supplement supplement1 = supplementRepository.save(supplement);

        StarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2);
        starRateService.createStarRate(supplement1.getId(),5);

        Assertions.assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(3.5);
    }

    @Test
    public void 별점_조회_실패(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("test");

        starRateService.createStarRate(supplement.getId(),2); //영양제 없는거로 조회하니까 에러 발생

    }

    @Test
    public void 별점_수정_성공(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("test");
        Supplement supplement1 = supplementRepository.save(supplement);

        StarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2);
        starRateService.updateStarRate(supplement1.getId(),starRateDto.getId(),5);

        Assertions.assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(5.0);
    }

}