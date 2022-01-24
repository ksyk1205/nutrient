package mandykr.nutrient.repository.combination;

import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import mandykr.nutrient.repository.SupplementCombinationRepository;
import mandykr.nutrient.repository.combination.starrate.CombinationStarRateRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
public class CombinationRepositoryTest {

    @Autowired
    CombinationStarRateRepository combinationStarRateRepository;
    @Autowired
    CombinationRepository combinationRepository;



    @Test
    public void 영양제_페치조인_조회(){
        Combination combination = combinationRepository.save(Combination.builder()
                .caption("영양제조합")
                .rating(0.0)
                .build());

        CombinationStarRate save1 = CombinationStarRate.builder()
                .starNumber(2)
                .build();

        save1.addStarRate(combination);
        CombinationStarRate save2 = CombinationStarRate.builder()
                .starNumber(2)
                .build();

        save2.addStarRate(combination);
        combinationStarRateRepository.save(save1);
        combinationStarRateRepository.save(save2);
        Assertions.assertEquals(combination.getCombinationStarRates().size(),
                combinationRepository.findByIdFetch(combination.getId()).get().getCombinationStarRates().size());

    }

}
