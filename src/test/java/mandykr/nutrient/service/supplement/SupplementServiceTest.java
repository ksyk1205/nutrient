package mandykr.nutrient.service.supplement;


import mandykr.nutrient.dto.supplement.*;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class SupplementServiceTest {

    private SupplementRepository supplementRepository = mock(SupplementRepository.class);
    private SupplementCategoryRepository supplementCategoryRepository = mock(SupplementCategoryRepository.class);

    private SupplementService supplementService = new SupplementService(supplementRepository, supplementCategoryRepository);

    SupplementCategory category;
    Supplement supplement;
    Supplement supplement2;

    @BeforeEach
    void before(){
        category = SupplementCategory.toEntity(1L, "자식 카테고리", 1);

        supplement = Supplement.builder()
                .id(2L)
                .name("testSupplement1")
                .prdlstReportNo("111-123")
                .ranking(0.0)
                .supplementCategory(category)
                .deleteFlag(false).build();

        supplement2 = Supplement.builder()
                .id(3L)
                .name("testSupplement2")
                .prdlstReportNo("111-222")
                .ranking(0.0)
                .supplementCategory(category)
                .deleteFlag(false).build();
    }

    @Test
    void 영양제_단건_조회(){
        //given

        //when
        when(supplementRepository.findById(2L)).thenReturn(Optional.of(supplement));
        SupplementResponseDto searchSupplement = supplementService.getSupplement(2L);

        //then
        then(supplementRepository).should(times(1)).findById(2L);
        assertThat(searchSupplement.getId()).isEqualTo(supplement.getId());
        assertThat(searchSupplement.getName()).isEqualTo(supplement.getName());
    }

    @Test
    void 영양제_전체_조회(){
        //given
        List<SupplementSearchResponse> supplementList = new ArrayList<>();
        supplementList.add(new SupplementSearchResponse(supplement.getId(),
                supplement.getName(),
                supplement.getPrdlstReportNo(),
                supplement.getRanking(),
                supplement.isDeleteFlag(),
                new SupplementSearchResponse.SupplementCategoryDto(category.getId(),
                                                                    category.getName())));

        supplementList.add( new SupplementSearchResponse(supplement2.getId(),
                supplement2.getName(),
                supplement2.getPrdlstReportNo(),
                supplement2.getRanking(),
                supplement2.isDeleteFlag(),
                new SupplementSearchResponse.SupplementCategoryDto(category.getId(),
                                                                    category.getName())));
        SupplementSearch supplementSearch = new SupplementSearch();
        //when
        when(supplementRepository.searchSupplementList(supplementSearch)).thenReturn(supplementList);
        List<SupplementSearchResponse> supplementDtoList = supplementService.getSupplementList(supplementSearch);
        //then
        assertThat(supplementDtoList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("카테고리별 영양제 조회")
    public void 영양제_카테고리_조회(){
        //given
        List<SupplementSearchResponse> supplementList = new ArrayList<>();
        supplementList.add(new SupplementSearchResponse(supplement.getId(),
                supplement.getName(),
                supplement.getPrdlstReportNo(),
                supplement.getRanking(),
                supplement.isDeleteFlag(),
                new SupplementSearchResponse.SupplementCategoryDto(category.getId(),
                        category.getName())));


        SupplementSearch supplementSearch = new SupplementSearch(1L,null);

        //when
        when(supplementRepository.searchSupplementList(supplementSearch)).thenReturn(supplementList);
        List<SupplementSearchResponse> supplementDtoList = supplementService.getSupplementList(supplementSearch);
        //then
        assertThat(supplementDtoList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("영양제 이름으로 조회")
    public void 영양제이름_조회() {
        //given
        List<SupplementSearchResponse> supplementList = new ArrayList<>();
        supplementList.add(new SupplementSearchResponse(supplement.getId(),
                supplement.getName(),
                supplement.getPrdlstReportNo(),
                supplement.getRanking(),
                supplement.isDeleteFlag(),
                new SupplementSearchResponse.SupplementCategoryDto(category.getId(),
                        category.getName())));


        SupplementSearch supplementSearch = new SupplementSearch(null,"test");

        //when
        when(supplementRepository.searchSupplementList(supplementSearch)).thenReturn(supplementList);
        List<SupplementSearchResponse> supplementDtoList = supplementService.getSupplementList(supplementSearch);
        //then
        assertThat(supplementDtoList.size()).isEqualTo(1);
    }

    @Test
    void 영양제_등록(){
        //given
        SupplementRequest supplementRequest = new SupplementRequest(supplement.getName(), supplement.getPrdlstReportNo());
        //when
        when(supplementCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(supplementRepository.save(isA(Supplement.class))).thenReturn(supplement);

        SupplementResponseDto testSupplement1 = supplementService.createSupplement(new SupplementRequestDto(supplementRequest),category.getId());
        //then
        assertThat(testSupplement1.getName()).isEqualTo(supplement.getName());

    }

    @Test
    void 영양제_수정(){
        //given
        SupplementRequest supplementDto = new SupplementRequest("testSupplement1", "111-123");
        //when

        when(supplementCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        SupplementResponseDto updateSupplement = supplementService.updateSupplement(category.getId(),supplement.getId(),new SupplementRequestDto(supplementDto));
        //then
        assertThat(updateSupplement.getId()).isEqualTo(supplement.getId());
    }

    @Test
    void 영양제_삭제(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        supplementService.deleteSupplement(supplement.getId());
        //then
        assertTrue(supplement.isDeleteFlag());
    }

    @Test
    void 영양제_콤보박스(){
        //given
        List<SupplementSearchComboResponse> supplementDtoList = new ArrayList<>();
        supplementDtoList.add(new SupplementSearchComboResponse(supplement.getId(),supplement.getName()));
        supplementDtoList.add(new SupplementSearchComboResponse(supplement2.getId(),supplement2.getName()));
        SupplementSearchCombo supplementSearchCombo = new SupplementSearchCombo("test");
        //when
        when(supplementRepository.searchCombo(supplementSearchCombo)).thenReturn(supplementDtoList);
        List<SupplementSearchComboResponse> supplementList = supplementService.getSearchCombo(supplementSearchCombo);

        //then
        assertThat(supplementDtoList.size()).isEqualTo(supplementList.size());
    }

}