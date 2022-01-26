package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.CombinationCreateRequest;
import mandykr.nutrient.dto.combination.CombinationDetailDto;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.combination.CombinationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CombinationServiceTest")
class CombinationServiceTest {

    CombinationRepository combinationRepository = mock(CombinationRepository.class);
    SupplementCombinationService supplementCombinationService = mock(SupplementCombinationService.class);


    CombinationService combinationService = new CombinationServiceImpl(combinationRepository, supplementCombinationService);


    Combination combination;
    Member member;

    @BeforeEach
    public void setup(){
        member = new Member();
        member.setName("bro");
        member.setMemberId("TEST");
        member.setId(1L);

        combination = Combination.builder()
                .id(1L)
                .caption("TEST")
                .build();
    }

    @Test
    public void 등록_테스트(){
        //given
        List<Long> supplementIds = Arrays.asList(1L,2L,3L);
        CombinationCreateRequest combinationCreateRequest = new CombinationCreateRequest("TEST", supplementIds);
        List<CombinationDetailDto.SupplementDto> supplementDtos = new ArrayList<>();

        CombinationDetailDto.SupplementDto supplementDto1 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(0)).name("영양제1").categoryId(1L).categoryName("비타민A").build();
        supplementDtos.add(supplementDto1);
        CombinationDetailDto.SupplementDto supplementDto2 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(1)).name("영양제2").categoryId(2L).categoryName("비타민B").build();
        supplementDtos.add(supplementDto2);
        CombinationDetailDto.SupplementDto supplementDto3 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(2)).name("영양제3").categoryId(3L).categoryName("비타민C").build();
        supplementDtos.add(supplementDto3);

        CombinationDetailDto combinationDetailDto = CombinationDetailDto.builder()
                .id(1L)
                .caption("TEST")
                .rating(0.0)
                .supplementDtoList(supplementDtos)
                .build();


        //when
        when(combinationRepository.save(any(Combination.class))).thenReturn(combination);
        when(combinationRepository.searchByCombination(combination)).thenReturn(combinationDetailDto);
        CombinationDetailDto saveCombinationDto = combinationService.createCombination(combinationCreateRequest);

        //then
        assertEquals(saveCombinationDto.getId(), combination.getId());
        assertEquals(saveCombinationDto.getCaption(), combination.getCaption());
        assertEquals(saveCombinationDto.getRating(), combination.getRating());
        List<CombinationDetailDto.SupplementDto> supplementDtoList = saveCombinationDto.getSupplementDtoList();
        for(int i = 0; i< supplementDtoList.size(); ++i){
            Long findId = supplementDtoList.get(i).getId();
            CombinationDetailDto.SupplementDto supplement = supplementDtos.stream().filter(o -> o.getId() == findId).findFirst().get();
            assertThat(supplementDtoList.get(i).getId()).isEqualTo(supplement.getId());
            assertThat(supplementDtoList.get(i).getName()).isEqualTo(supplement.getName());
            assertThat(supplementDtoList.get(i).getCategoryId()).isEqualTo(supplement.getCategoryId());
            assertThat(supplementDtoList.get(i).getCategoryName()).isEqualTo(supplement.getCategoryName());
        }
    }
}