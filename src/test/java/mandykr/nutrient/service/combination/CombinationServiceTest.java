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
    SupplementRepository supplementRepository = mock(SupplementRepository.class);
    SupplementCombinationRepository supplementCombinationRepository = mock(SupplementCombinationRepository.class);


    CombinationService combinationService = new CombinationServiceImpl(combinationRepository, supplementRepository, supplementCombinationRepository);

    List<Supplement> supplements = new ArrayList<>();
    Supplement supplement1;
    Supplement supplement2;
    Supplement supplement3;
    Member member;
    List<SupplementCombination> supplementCombinations = new ArrayList<>();



    @BeforeEach
    public void setup(){
        supplement1 = Supplement.builder()
                .id(1L)
                .name("영양제1")
                .build();
        supplement2 = Supplement.builder()
                .id(2L)
                .name("영양제2")
                .build();
        supplement3 = Supplement.builder()
                .id(3L)
                .name("영양제3")
                .build();

        supplements.add(supplement1);
        supplements.add(supplement2);
        supplements.add(supplement3);
        
        member = new Member();
        member.setId(1L);
        member.setMemberId("TEST");
        member.setName("TEST_계정");



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
        supplementCombinations.add(new SupplementCombination(supplement1, combination));
        supplementCombinations.add(new SupplementCombination(supplement2, combination));
        supplementCombinations.add(new SupplementCombination(supplement3, combination));

        //when
        when(supplementRepository.findAllById(supplementIds)).thenReturn(supplements);
        when(combinationRepository.save(any(Combination.class))).thenReturn(combination);
        when(supplementCombinationRepository.saveAll(anyList())).thenReturn(supplementCombinations);
        CombinationDto saveCombinationDto = combinationService.createCombination(combinationCreateDto, member);

        //then
        assertEquals(saveCombinationDto.getId(), combination.getId());
        assertEquals(saveCombinationDto.getCaption(), combination.getCaption());
        assertEquals(saveCombinationDto.getRating(), combination.getRating());

    }
}