package mandykr.nutrient.repository.combination;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.dto.combination.*;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.supplement.SupplementCategoryRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
class CombinationRepositoryCustomTest {
    @Autowired CombinationRepository combinationRepository;

    @Autowired
    SupplementRepository supplementRepository;

    @Autowired
    SupplementCategoryRepository supplementCategoryRepository;

    @Autowired
    SupplementCombinationRepository supplementCombinationRepository;

    SupplementCategory parentCategory;
    SupplementCategory category;

    List<Supplement> supplements = new ArrayList<>();
    Supplement supplement1;
    Supplement supplement2;
    Supplement supplement3;

    Combination combination;
    List<SupplementCombination> supplementCombinations;
    @BeforeEach
    public void setup(){
        parentCategory = supplementCategoryRepository.save(SupplementCategory.builder().name("오메가369/피쉬오일").depth(0).build());
        category = supplementCategoryRepository.save(SupplementCategory.builder().name("오메가3").depth(1).parentCategory(parentCategory).build());


        supplement1 = supplementRepository.save(Supplement.builder().supplementCategory(category).name("비타민A").prdlstReportNo("1-2-3").ranking(0.0).build());
        supplements.add(supplement1);
        supplement2 = supplementRepository.save(Supplement.builder().supplementCategory(category).name("비타민B").prdlstReportNo("2-2-3").ranking(0.0).build());
        supplements.add(supplement2);
        supplement3 = supplementRepository.save(Supplement.builder().supplementCategory(category).name("비타민B").prdlstReportNo("2-2-3").ranking(0.0).build());
        supplements.add(supplement3);

        combination = combinationRepository.save(Combination.builder().caption("test").build());
        supplementCombinations = supplementCombinationRepository.saveAll(supplements.stream().map(supplement -> new SupplementCombination(supplement, combination)).collect(Collectors.toList()));
    }


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
        categoryList.add(new CombinationConditionCategory(parentCategory.getId()));
        categoryList.add(new CombinationConditionCategory(category.getId()));

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

    @Test
    void searchByCombination() {
        //given

        //when
        CombinationDetailDto combinationDetailDto = combinationRepository.searchByCombination(combination);

        //then
        assertThat(combinationDetailDto.getId()).isEqualTo(combination.getId());
        assertThat(combinationDetailDto.getCaption()).isEqualTo(combination.getCaption());
        assertThat(combinationDetailDto.getRating()).isEqualTo(combination.getRating());
        List<CombinationDetailDto.SupplementDto> supplementDtoList = combinationDetailDto.getSupplementDtoList();
        for(int i = 0; i< supplementDtoList.size(); ++i){
            Long findId = supplementDtoList.get(i).getId();
            Supplement supplement = supplements.stream().filter(o -> o.getId() == findId).findFirst().get();
            assertThat(supplementDtoList.get(i).getId()).isEqualTo(supplement.getId());
            assertThat(supplementDtoList.get(i).getName()).isEqualTo(supplement.getName());
            assertThat(supplementDtoList.get(i).getCategoryId()).isEqualTo(supplement.getSupplementCategory().getId());
            assertThat(supplementDtoList.get(i).getCategoryName()).isEqualTo(supplement.getSupplementCategory().getName());
        }


    }
}