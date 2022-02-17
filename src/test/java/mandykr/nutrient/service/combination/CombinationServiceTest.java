package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.CombinationCreateRequest;
import mandykr.nutrient.dto.combination.CombinationDetailDto;
import mandykr.nutrient.dto.combination.CombinationUpdateRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.combination.CombinationRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CombinationServiceTest")
class CombinationServiceTest {

    CombinationRepository combinationRepository = mock(CombinationRepository.class);
    SupplementCombinationService supplementCombinationService = mock(SupplementCombinationService.class);
    SupplementRepository supplementRepository = mock(SupplementRepository.class);

    CombinationService combinationService =
            new CombinationServiceImpl(combinationRepository, supplementCombinationService, supplementRepository);


    //Member member;
    Long combinationId = 1L;
    Combination combination;
    List<Long> supplementIds;
    List<Supplement> supplements;
    List<SupplementCombination> supplementCombinations = new ArrayList<>();

    @BeforeEach
    void setUp() {
        //member = new Member();
        //member.setName("bro");
        //member.setMemberId("TEST");
        //member.setId(1L);

        supplementIds = LongStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());
        supplements = new ArrayList<>();
        supplementIds.forEach(id -> {
            Supplement supplement = Supplement.builder().id(id).build();
            supplements.add(supplement);
            supplementCombinations.add(SupplementCombination.builder()
                    .id(id)
                    .combination(combination)
                    .supplement(supplement)
                    .build());
        });
        combination = Combination.builder().id(combinationId).supplementCombinations(supplementCombinations).build();

    }

    @Test
    public void 등록_테스트(){
        //given
        combination = Combination.builder()
                .id(1L)
                .caption("TEST")
                .build();

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


    @Test
    @DisplayName("영양제 id 리스트를 전달받아 조합의 영양제 목록을 수정한다")
    void updateCombinationToRemoveSupplements() {
        // given
        String updateCaption = "update caption";
        List<Long> updateSupplementIds = new ArrayList<>();
        CombinationUpdateRequest request = new CombinationUpdateRequest(updateCaption, updateSupplementIds);
        int size = supplements.size();
        supplements.remove(0);

        given(combinationRepository.findById(combinationId)).willReturn(Optional.of(combination));
        given(supplementRepository.findAllById(updateSupplementIds)).willReturn(supplements);
        given(combinationRepository.searchByCombination(combination)).willReturn(new CombinationDetailDto());

        // when
        combinationService.updateCombination(combinationId, request);

        // then
        assertEquals(size - 1, combination.getSupplementCombinations().size());
    }

    @Test
    @DisplayName("영양제 id 리스트를 전달받아 조합의 영양제 목록을 수정한다")
    void updateCombinationToAddSupplements() {
        // given
        String updateCaption = "update caption";
        List<Long> updateSupplementIds = new ArrayList<>();
        CombinationUpdateRequest request = new CombinationUpdateRequest(updateCaption, updateSupplementIds);
        int size = supplements.size();
        supplements.add(Supplement.builder().id(6L).build());

        given(combinationRepository.findById(combinationId)).willReturn(Optional.of(combination));
        given(supplementRepository.findAllById(updateSupplementIds)).willReturn(supplements);
        given(combinationRepository.searchByCombination(combination)).willReturn(new CombinationDetailDto());

        // when
        combinationService.updateCombination(combinationId, request);

        // then
        assertEquals(size + 1, combination.getSupplementCombinations().size());
    }

    @Test
    @DisplayName("영양제조합 ID로 삭제")
    void 영양제_제거() {
        // given

        // when
        when(combinationRepository.findById(combinationId)).thenReturn(Optional.of(combination));
        combinationService.deleteCombination(combinationId);

        // then
        then(combinationRepository).should(times(1)).delete(combination);

    }


}