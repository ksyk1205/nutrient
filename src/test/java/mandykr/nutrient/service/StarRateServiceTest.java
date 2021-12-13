package mandykr.nutrient.service;

import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.entity.StarRate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StarRateServiceTest {

    @Autowired
    StarRateService starRateService;

    @Test
    public void createTest(){
        StarRateDto starRateDto = new StarRateDto();
        starRateDto.setStarNumber(12L);
        StarRateDto tmpDto = starRateService.createStarRate(starRateDto);

        Assertions.assertEquals(tmpDto.getId(),starRateService.getStarRate(tmpDto.getId()).getId());
    }

    @Test
    public void getTest(){
        StarRateDto starRateDto = new StarRateDto();
        starRateDto.setStarNumber(12L);
        StarRateDto tmpDto = starRateService.createStarRate(starRateDto);

        Assertions.assertEquals(1,starRateService.getStarRateList().size());
    }

}