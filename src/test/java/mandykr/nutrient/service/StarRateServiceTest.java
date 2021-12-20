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
    public void createStarRate(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("test");
        Supplement supplement1 = supplementRepository.save(supplement);

        StarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2L);
        starRateService.createStarRate(supplement1.getId(),5L);

        Assertions.assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(3.5);
    }

    @Test
    public void updateStarRate(){
        Supplement supplement = new Supplement();
        supplement.setRanking(0.0);
        supplement.setName("test");
        Supplement supplement1 = supplementRepository.save(supplement);

        StarRateDto starRateDto = starRateService.createStarRate(supplement1.getId(),2L);
        starRateService.updateStarRate(supplement1.getId(),starRateDto.getId(),5L);

        Assertions.assertThat(supplementRepository.findById(supplement1.getId()).get().getRanking()).isEqualTo(5.0);
    }

}