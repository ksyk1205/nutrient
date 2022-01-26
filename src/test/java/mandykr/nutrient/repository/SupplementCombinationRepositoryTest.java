package mandykr.nutrient.repository;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.combination.CombinationRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import(TestConfig.class)
class SupplementCombinationRepositoryTest {

    @Autowired
    SupplementCombinationRepository supplementCombinationRepository;

    @Autowired
    CombinationRepository combinationRepository;

    @Autowired
    SupplementRepository supplementRepository;

    Supplement supplement1;
    Supplement supplement2;
    Supplement supplement3;

    Combination combination1;

    @BeforeEach
    public void setup(){
        supplement1 = supplementRepository.save(Supplement
                .builder()
                .name("영양제1")
                .prdlstReportNo("111-111")
                .ranking(0.0)
                .deleteFlag(false)
                .build());

        supplement2 = supplementRepository.save(Supplement
                .builder()
                .name("영양제2")
                .prdlstReportNo("222-222")
                .ranking(0.0)
                .deleteFlag(false)
                .build());

        supplement3 = supplementRepository.save(Supplement
                .builder()
                .name("영양제3")
                .prdlstReportNo("333-333")
                .ranking(0.0)
                .deleteFlag(false)
                .build());
        combination1 = combinationRepository.save(Combination.builder()
                .caption("영양제조합")
                .rating(0.0)
                .build());
    }

    @Test
    public void 메핑테이블등록테스트(){
        //given
        List<SupplementCombination> supplementCombinations = new ArrayList<>();
        supplementCombinations.add(new SupplementCombination(supplement1, combination1));
        supplementCombinations.add(new SupplementCombination(supplement2, combination1));
        supplementCombinations.add(new SupplementCombination(supplement3, combination1));


        //when
        List<SupplementCombination> saveSupplementCombinations = supplementCombinationRepository.saveAll(supplementCombinations);


        //then
        assertEquals(saveSupplementCombinations.size(), 3);
    }
}