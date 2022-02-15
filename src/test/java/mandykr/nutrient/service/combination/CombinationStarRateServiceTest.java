package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.starRate.CombinationStarRateDto;
import mandykr.nutrient.dto.combination.starRate.request.CombinationStarRateRequest;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.Member;

import mandykr.nutrient.repository.combination.starrate.CombinationStarRateRepository;

import mandykr.nutrient.repository.combination.CombinationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayName("CombinationStarRateServiceTest")
class CombinationStarRateServiceTest {
    CombinationRepository combinationRepository = mock(CombinationRepository.class);

    CombinationStarRateRepository combinationStarRateRepository = mock(CombinationStarRateRepository.class);

    CombinationStarRateService combinationStarRateService = new CombinationStarRateService(combinationStarRateRepository,combinationRepository);

    Combination combination;
    Member member;
    CombinationStarRate combinationStarRate;


    @BeforeEach
    public void setup(){
        //조합 등록
        combination = Combination.builder()
                .id(1L)
                .rating(0.0)
                .build();
        member = new Member();
        //member.setId(1L);
        //member.setMemberId("testMember");
        //member.setName("KIM");
        combinationStarRate = CombinationStarRate
                .builder()
                .id(1L)
                .member(member)
                .combination(combination)
                .starNumber(2)
                .build();

        given(combinationRepository.findByIdFetch(combination.getId())).willReturn(Optional.ofNullable(combination));
    }

    @Test
    @DisplayName("영양제 조합 별점 등록")
    public void 영양제_조합_별점_등록(){
        //given
        CombinationStarRateRequest combinationStarRateRequest = new CombinationStarRateRequest(2);

        //when
        when(combinationStarRateRepository.findByCombinationIdAndMember(anyLong(),any(Member.class))).thenReturn(Optional.empty());
        when(combinationStarRateRepository.save(any(CombinationStarRate.class))).thenReturn(combinationStarRate);
        CombinationStarRateDto combinationStarRateDto = combinationStarRateService.createCombinationStarRate(combination.getId(), member, combinationStarRateRequest);

        //then
        assertEquals(combinationStarRateDto.getStarNumber(),combinationStarRateRequest.getStarNumber());

    }

    @Test
    @DisplayName("영양제 조합 별점 등록 오류(영양제 존재X)")
    public void 영양제_조합_별점_등록_영양제_존재X(){
        CombinationStarRateRequest combinationStarRateRequest = new CombinationStarRateRequest(2);

        when(combinationRepository.findByIdFetch(combination.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.createCombinationStarRate(combination.getId(), member, combinationStarRateRequest),
                combination.getId() + "의 영양제 조합이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("영양제 조합 별점 등록 오류(영양제 조합 별점 존재)")
    public void 영양제_조합_별점_등록_영양제_조합_별점_존재(){
        //given
        CombinationStarRateRequest combinationStarRateRequest = new CombinationStarRateRequest(2);

        //when
        when(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(), member))
                .thenReturn(Optional.of(CombinationStarRate.builder().build()));

        //then
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.createCombinationStarRate(combination.getId(), member, combinationStarRateRequest)
        , "등록한 영양제 조합 별점이 존재합니다");
    }

    @Test
    @DisplayName("영양제 조합 별점 수정")
    public void 영양제_조합_별점_수정(){
        //given
        combinationStarRate.addStarRate(combination);
        CombinationStarRateRequest combinationStarRateRequest = new CombinationStarRateRequest(4);

        //when
        when(combinationRepository.findByIdFetch(anyLong())).thenReturn(Optional.of(combination));
        when(combinationStarRateRepository.findIdAndMemberAndComb(combination.getId(), member,  combinationStarRate.getId())).thenReturn(Optional.of(combinationStarRate));
        CombinationStarRateDto combinationStarRateDto =
                combinationStarRateService.updateCombinationStarRate(combination.getId(), combinationStarRate.getId(), member, combinationStarRateRequest);

        assertEquals(combinationStarRateDto.getStarNumber(), combinationStarRateRequest.getStarNumber());
    }

    @Test
    @DisplayName("영양제 조합 별점 수정 오류(영양제 조합 존재X)")
    public void 영양제_조합_별점_수정_영양제_조합_존재X(){
        //given
        CombinationStarRateRequest combinationStarRateRequest = new CombinationStarRateRequest(4);
        //when
        when(combinationRepository.findByIdFetch(combination.getId())).thenReturn(Optional.empty());

        //then
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.updateCombinationStarRate(combination.getId(), combinationStarRate.getId(), member, combinationStarRateRequest),
                combination.getId() + "의 영양제 조합이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("영양제 조합 별점 수정 오류(영양제 조합 별점 존재X)")
    public void 영양제_조합_별점_수정_영양제_조합_별점_존재X(){
        //given
        CombinationStarRateRequest combinationStarRateRequest = new CombinationStarRateRequest(4);

        //when
        when(combinationStarRateRepository.findIdAndMemberAndComb(combination.getId(), member, combinationStarRate.getId()))
                .thenReturn(Optional.empty());

        //then
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.updateCombinationStarRate(combination.getId(), combinationStarRate.getId(), member, combinationStarRateRequest),
                combinationStarRate.getId() + "의 영양제 조합 별점이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("영양제 조합 별점 조회")
    public void 영양제_조합_별점_조회(){
        //given

        //when
        when(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(), member))
                .thenReturn(Optional.of(combinationStarRate));
        
        CombinationStarRateDto combinationStarRateDto =
                combinationStarRateService.getCombinationStarRateByCombination(combination.getId(), member);

        //then
        assertEquals(combinationStarRateDto.getId(), combinationStarRate.getId());
        assertEquals(combinationStarRateDto.getStarNumber(), combinationStarRate.getStarNumber());
    }

    @Test
    @DisplayName("영양제 조합 별점 조회(빈객체)")
    public void 영양제_조합_별점_조회_빈객체(){
        //given

        //when
        when(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(), member))
                .thenReturn(Optional.empty());

        CombinationStarRateDto combinationStarRateDto =
                combinationStarRateService.getCombinationStarRateByCombination(combination.getId(), member);

        //then
        assertNull(combinationStarRateDto.getId());
        assertNull(combinationStarRateDto.getStarNumber());
    }
    
    @Test
    @DisplayName("영양제 조합 별점 삭제")
    public void 영양제_조합_별점_삭제(){
        //given

        //when
        when(combinationStarRateRepository.findIdAndMember(combinationStarRate.getId(), member))
                .thenReturn(Optional.of(combinationStarRate));
        combinationStarRateService.deleteCombinationStarRate(combinationStarRate.getId(), member);
        //then

        then(combinationStarRateRepository).should(times(1)).deleteById(combinationStarRate.getId());
    }

    @Test
    @DisplayName("영양제 조합 별점 삭제_영양제조합별점 없음")
    public void 영양제_조합_별점_삭제_영양제조합별점_없음(){
        //given

        //when
        when(combinationStarRateRepository.findIdAndMember(combinationStarRate.getId(), member)).thenReturn(Optional.empty());
        //then
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.deleteCombinationStarRate(combinationStarRate.getId(), member),
                combinationStarRate.getId() + "의 영양제 조합 별점이 존재하지 않습니다.");
    }
}