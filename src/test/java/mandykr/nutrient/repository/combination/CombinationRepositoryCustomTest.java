package mandykr.nutrient.repository.combination;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.dto.combination.CombinationConditionCategory;
import mandykr.nutrient.dto.combination.CombinationConditionSupplement;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Import(TestConfig.class)
class CombinationRepositoryCustomTest {
    @Autowired CombinationRepository combinationRepository;


    @Test
    void findWithSupplement() {
//        SupplementCategory category1 = SupplementCategory.builder().name("category1").build();
//        SupplementCategory category2 = SupplementCategory.builder().name("category2").build();
//        SupplementCategory category3 = SupplementCategory.builder().name("category3").build();
//        SupplementCategory category4 = SupplementCategory.builder().name("category4").build();
//        SupplementCategory category5 = SupplementCategory.builder().name("category5").build();
//
//        Supplement supplement1 = Supplement.builder().name("supplement1").supplementCategory(category1).build();
//        Supplement supplement2 = Supplement.builder().name("supplement2").supplementCategory(category2).build();
//        Supplement supplement3 = Supplement.builder().name("supplement3").supplementCategory(category3).build();
//        Supplement supplement4 = Supplement.builder().name("supplement4").supplementCategory(category4).build();
//        Supplement supplement5 = Supplement.builder().name("supplement5").supplementCategory(category5).build();
//
//        Combination combination1 = Combination.builder().caption("combination1").build();
//        Combination combination2 = Combination.builder().caption("combination2").build();
//        Combination combination3 = Combination.builder().caption("combination3").build();


        Pageable pageable = new PageRequestUtil().getPageable();
        Page<CombinationDto> combinationDtoPage = combinationRepository.findWithSupplement(pageable);
        combinationDtoPage.forEach(d -> {
            System.out.println(d.getCaption());
            d.getSupplementDtoList().forEach(s -> {
                System.out.println(s.getName());
                System.out.println(s.getCategoryName());
            });
            System.out.println();
        });
    }

    @Test
    void searchBySupplementList() {
        List<CombinationConditionSupplement> supplementList = new ArrayList<>();
        supplementList.add(new CombinationConditionSupplement(3L));
        supplementList.add(new CombinationConditionSupplement(5L));

        Pageable pageable = new PageRequestUtil().getPageable();
        Page<CombinationDto> combinationDtoPage = combinationRepository.searchBySupplementList(supplementList, pageable);
        combinationDtoPage.forEach(d -> {
            System.out.println(d.getCaption());
            d.getSupplementDtoList().forEach(s -> {
                System.out.println(s.getName());
                System.out.println(s.getCategoryName());
            });
            System.out.println();
        });
    }

    @Test
    void searchByCategoryList() {
        List<CombinationConditionCategory> categoryList = new ArrayList<>();
        categoryList.add(new CombinationConditionCategory(3L));
        categoryList.add(new CombinationConditionCategory(5L));

        Pageable pageable = new PageRequestUtil().getPageable();
        Page<CombinationDto> combinationDtoPage = combinationRepository.searchByCategoryList(categoryList, pageable);
        combinationDtoPage.forEach(d -> {
            System.out.println(d.getCaption());
            d.getSupplementDtoList().forEach(s -> {
                System.out.println(s.getName());
                System.out.println(s.getCategoryName());
            });
            System.out.println();
        });
    }
}