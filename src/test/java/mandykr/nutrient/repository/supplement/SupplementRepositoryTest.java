package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.dto.supplement.*;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Pageable pageable;

    @BeforeEach
    void before() {
        parentCategory = categoryRepository.save(SupplementCategory.builder().name("오메가369/피쉬오일").depth(0).build());
        category = categoryRepository.save(SupplementCategory.builder().name("오메가3").depth(1).parentCategory(parentCategory).build());

        supplement1 = Supplement.builder().supplementCategory(category).name("비타민A").prdlstReportNo("1-2-3").ranking(0.0).build();
        supplement2 = Supplement.builder().supplementCategory(category).name("비타민B").prdlstReportNo("2-2-3").ranking(0.0).build();

        pageable = new PageRequestUtil(1, 2).getPageable();
    }

    @Test
    public void 영양제_등록(){
        //given
        Supplement save = supplementRepository.save(supplement1);
        //when
        Optional<Supplement> findById = supplementRepository.findById(save.getId());
        assertEquals(findById.get().getName(), save.getName());
    }

    @Test
    @DisplayName("영양제 전체 조회(검색조건 X)")
    public void 영양제_전체_조회(){
        //given
        supplementRepository.save(supplement1);
        supplementRepository.save(supplement2);
        SupplementSearchRequest supplementSearch = new SupplementSearchRequest();
        //when
        Page<SupplementSearchResponse> findAll = supplementRepository.searchSupplementList(supplementSearch, pageable);
        //then
        assertEquals(findAll.getContent().size(), 2);
    }

    @Test
    @DisplayName("카테고리별 영양제 조회")
    public void 영양제_카테고리_조회(){
        //given
        Supplement supplement3 = Supplement.builder().supplementCategory(parentCategory).name("비타민C").prdlstReportNo("7-8-9").ranking(0.0).build();
        supplementRepository.save(supplement1);
        supplementRepository.save(supplement2);
        supplementRepository.save(supplement3);
        SupplementSearchRequest supplementSearch = new SupplementSearchRequest(parentCategory.getId(), null);

        //when
        Page<SupplementSearchResponse> findAll = supplementRepository.searchSupplementList(supplementSearch, pageable);
        //then
        assertEquals(findAll.getContent().size(),1);
    }


    @Test
    @DisplayName("영양제 이름으로 조회")
    public void 영양제이름_조회(){
        //given
        supplementRepository.save(supplement1);
        supplementRepository.save(supplement2);
        SupplementSearchRequest supplementSearch = new SupplementSearchRequest(null, "B");

        //when
        Page<SupplementSearchResponse> findAll = supplementRepository.searchSupplementList(supplementSearch, pageable);
        //then
        assertEquals(findAll.getContent().size(),1);
    }

    @Test
    public void 영양제_수정(){
        //given
        Supplement supplement = supplementRepository.save(supplement1);
        SupplementRequest supplementRequest = new SupplementRequest("비타민C", supplement.getPrdlstReportNo());
        //when
        supplement.updateNameAndPrdlstAndCategory(supplementRequest.getName(), supplementRequest.getPrdlstReportNo(), category);
        //then
        assertEquals(supplementRepository.findById(supplement.getId()).get().getName(), "비타민C");

    }

    @Test
    @DisplayName("영양제 콤보 조회")
    void 영양제_콤보(){
        //given
        supplementRepository.save(supplement1);
        supplementRepository.save(supplement2);
        //when
        List<SupplementSearchComboResponse> supplementDtoList = supplementRepository.searchSupplementCombo(new SupplementSearchComboRequest("비민"));
        //then
        assertEquals(supplementDtoList.size(),0);

    }

}