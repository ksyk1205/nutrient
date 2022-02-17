package mandykr.nutrient.repository.combination;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.combination.starrate.CombinationStarRateRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CombinationRepositoryTest {

    @Autowired
    CombinationStarRateRepository combinationStarRateRepository;

    @Autowired
    CombinationRepository combinationRepository;

    @Autowired
    SupplementCombinationRepository supplementCombinationRepository;

    @Autowired
    SupplementRepository supplementRepository;

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
    @Test
    @DisplayName("영양제 조합 id를 받아 삭제한다")
    void deleteToCombination() {
        // given
        Combination combination = combinationRepository.save(Combination.builder().caption("영양제조합1").rating(0.0).build());
        List<Supplement> supplements = new ArrayList<>();
        Supplement supplement1 = supplementRepository.save(Supplement.builder().build());
        Supplement supplement2 = supplementRepository.save(Supplement.builder().build());
        supplements.add(supplement1);
        supplements.add(supplement2);
        combination.addSupplementCombinations(supplements);


        // when
        combination.getSupplementCombinations().clear();
        List<SupplementCombination> saveSupplementCombinations = supplementCombinationRepository.findAll();

        // then
        Assertions.assertEquals(saveSupplementCombinations.size(), 0);
    }
}
