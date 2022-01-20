package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
class SupplementRepositoryTest {
    @Autowired
    SupplementRepository supplementRepository;
    @Autowired
    SupplementCategoryRepository categoryRepository;

    SupplementCategory parentCategory;
    SupplementCategory category;
    Supplement supplement1;
    Supplement supplement2;

    @BeforeEach
    void before() {
        parentCategory = SupplementCategory.builder().name("오메가369/피쉬오일").depth(0).build();
        category = SupplementCategory.builder().name("오메가3").depth(1).parentCategory(parentCategory).build();
        SupplementCategory saveParentCategory = categoryRepository.save(parentCategory);
        SupplementCategory saveCategory = categoryRepository.save(category);

        supplement1 = Supplement.builder().supplementCategory(category).name("비타민A").prdlstReportNo("1-2-3").ranking(0.0).build();
        supplement2 = Supplement.builder().supplementCategory(category).name("비타민B").prdlstReportNo("2-2-3").ranking(0.0).build();
    }

    @Test
    public void 영양제_등록(){
        //given
        Supplement save = supplementRepository.save(supplement1);
        //when
        Optional<Supplement> findById = supplementRepository.findById(save.getId());
        assertEquals(findById.get().getName(),save.getName());
    }

    @Test
    public void 영양제_전체_조회(){
        //given
        supplementRepository.save(supplement1);
        supplementRepository.save(supplement2);
        //when
        List<Supplement> findAll = supplementRepository.findAll();
        //then
        assertEquals(findAll.size(),2);
    }

    @Test
    public void 영양제_수정(){
        //given
        Supplement supplement = supplementRepository.save(supplement1);
        //when
        supplement.updateNameAndPrdlstAndCategory("비타민C",null,category);
        //then
        assertEquals(supplementRepository.findById(supplement.getId()).get().getName(),"비타민C");

    }

}