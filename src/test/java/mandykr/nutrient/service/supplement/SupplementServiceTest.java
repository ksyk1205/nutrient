package mandykr.nutrient.service.supplement;


import mandykr.nutrient.dto.supplement.SupplementDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class SupplementServiceTest {

    private SupplementRepository supplementRepository = mock(SupplementRepository.class);
    private SupplementCategoryRepository supplementCategoryRepository = mock(SupplementCategoryRepository.class);

    private SupplementService supplementService = new SupplementService(supplementRepository, supplementCategoryRepository);

    SupplementCategory category;
    Supplement supplement;

    @BeforeEach
    public void before(){
        category = SupplementCategory.toEntity(1L, "자식 카테고리", 1);

        supplement = Supplement.builder()
                            .id(2L)
                            .name("testSupplement1")
                            .prdlstReportNo("111-123")
                            .ranking(0.0)
                            .supplementCategory(category)
                            .deleteFlag(false).build();
    }

    @Test
    public void 영양제_단건_조회(){
        //given

        //when
        when(supplementRepository.findById(2L)).thenReturn(Optional.of(supplement));
        SupplementDto searchSupplement = supplementService.getSupplement(2L);

        //then
        then(supplementRepository).should(times(1)).findById(2L);
        assertThat(searchSupplement.getId()).isEqualTo(supplement.getId());
        assertThat(searchSupplement.getName()).isEqualTo(supplement.getName());
    }

    @Test
    public void 영양제_전체_조회(){
        //given

        Supplement supplement2 = Supplement.builder().id(3L).name("testSupplement2").prdlstReportNo("222-123").ranking(0.0).build();
        List<Supplement> supplementList = new ArrayList<>();
        supplementList.add(supplement);
        supplementList.add(supplement2);

        //when
        when(supplementRepository.findAll()).thenReturn(supplementList);
        List<SupplementDto> supplementDtoList = supplementService.getSupplementList();
        //then
        assertThat(supplementDtoList.size()).isEqualTo(2);
    }

    @Test
    public void 영양제_등록(){
        //given

        //when
        when(supplementCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(supplementRepository.save(isA(Supplement.class))).thenReturn(supplement);
        SupplementDto supplementDto = new SupplementDto(null,"testSupplement1", "111-123", 0.0);
        SupplementDto testSupplement1 = supplementService.createSupplement(supplementDto,category.getId());
        //then
        assertThat(testSupplement1.getName()).isEqualTo(supplement.getName());

    }

    @Test
    public void 영양제_수정(){
        //given

        //when
        SupplementDto supplementDto = new SupplementDto(supplement.getId(),"testSupplement1", "111-123", 0.0);
        when(supplementCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        SupplementDto updateSupplement = supplementService.updateSupplement(supplementDto, category.getId());
        //then
        assertThat(updateSupplement.getId()).isEqualTo(supplementDto.getId());
    }

    @Test
    public void 영양제_삭제(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        supplementService.deleteSupplement(supplement.getId());
        //then
        Assertions.assertTrue(supplement.isDeleteFlag());
    }
}