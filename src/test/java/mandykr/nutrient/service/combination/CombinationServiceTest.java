package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.CombinationCreateDto;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCombinationRepository;
import mandykr.nutrient.repository.combination.CombinationRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CombinationServiceTest")
class CombinationServiceTest {

    CombinationRepository combinationRepository = mock(CombinationRepository.class);
    SupplementCombinationService supplementCombinationService = mock(SupplementCombinationService.class);


    CombinationService combinationService = new CombinationServiceImpl(combinationRepository, supplementCombinationService);




    @BeforeEach
    public void setup(){

    }

    @Test
    public void 등록_테스트(){
        //given
        List<Long> supplementIds = Arrays.asList(1L,2L,3L);
        CombinationCreateDto combinationCreateDto = new CombinationCreateDto("TEST", supplementIds);
        Combination combination = Combination.builder()
                .id(1L)
                .caption("TEST")
                .caption(combinationCreateDto.getCaption())
                .rating(Combination.ZERO)
                .build();


        //when
        when(combinationRepository.save(any(Combination.class))).thenReturn(combination);
        CombinationDto saveCombinationDto = combinationService.createCombination(combinationCreateDto);

        //then
        assertEquals(saveCombinationDto.getId(), combination.getId());
        assertEquals(saveCombinationDto.getCaption(), combination.getCaption());
        assertEquals(saveCombinationDto.getRating(), combination.getRating());

    }
}