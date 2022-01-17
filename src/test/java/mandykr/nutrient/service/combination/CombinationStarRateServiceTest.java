package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.starRate.CombinationStarRateRequestDto;
import mandykr.nutrient.dto.combination.starRate.CombinationStarRateResponseDto;
import mandykr.nutrient.dto.combination.starRate.request.CombinationStarRateRequest;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.Member;

import mandykr.nutrient.repository.combination.starrate.CombinationStarRateRepository;

import mandykr.nutrient.repository.combination.CombinationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CombineStarRateServiceTest")
class CombinationStarRateServiceTest {
    CombinationRepository combinationRepository = mock(CombinationRepository.class);

    CombinationStarRateRepository combinationStarRateRepository = mock(CombinationStarRateRepository.class);

    CombinationStarRateService combinationStarRateService = new CombinationStarRateService(combinationStarRateRepository,combinationRepository);

    private Combination combination;
    private Member member;
    @BeforeEach
    public void setup(){
        //조합 등록
        combination = Combination.builder()
                .id(1L)
                .rating(0.0)
                .build();
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testMember");
        member.setName("KIM");
        this.member = member;

        given(combinationRepository.findByIdFetch(combination.getId())).willReturn(Optional.ofNullable(combination));
    }

    @Test
    @DisplayName("영양제 조합 별점 등록")
    public void 영양제_조합_별점_등록(){
        //given
        CombinationStarRateRequestDto combinationStarRateRequestDto = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());

        //when
        when(combinationStarRateRepository.findByCombinationIdAndMember(anyLong(),any(Member.class))).thenReturn(Optional.empty());
        when(combinationStarRateRepository.save(any(CombinationStarRate.class))).thenReturn(
            CombinationStarRate
                    .builder()
                    .id(1L)
                    .member(member)
                    .combination(combination)
                    .starNumber(combinationStarRateRequestDto.getStarNumber())
                    .build()
        );
        CombinationStarRateResponseDto combinationStarRateResponseDto = combinationStarRateService.createCombinationStarRate(combination.getId(), member, combinationStarRateRequestDto);

        //then
        assertEquals(combinationStarRateResponseDto.getStarNumber(),combinationStarRateRequestDto.getStarNumber());
    }

    @Test
    @DisplayName("영양제 조합 별점 등록 오류(영양제 존재X)")
    public void 영양제_조합_별점_등록_영양제_존재X(){
        CombinationStarRateRequestDto combinationStarRateRequestDto = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());

        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.createCombinationStarRate(2L, member, combinationStarRateRequestDto),
                "2의 영양제 조합이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("영양제 조합 별점 등록 오류(영양제 조합 별점 존재)")
    public void 영양제_조합_별점_등록_영양제_조합_별점_존재(){
        //given
        CombinationStarRateRequestDto combinationStarRateRequestDto1 = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());
        CombinationStarRateRequestDto combinationStarRateRequestDto2 = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(4).build());
        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .member(member)
                .starNumber(combinationStarRateRequestDto1.getStarNumber())
                .build();

        //when
        when(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(), member))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.ofNullable(insert));
        when(combinationStarRateRepository.save(any(CombinationStarRate.class)))
            .thenReturn(
                CombinationStarRate
                        .builder()
                        .id(1L)
                        .member(member)
                        .combination(combination)
                        .starNumber(combinationStarRateRequestDto1.getStarNumber())
                        .build()
        ).thenReturn(
                CombinationStarRate
                        .builder()
                        .id(1L)
                        .member(member)
                        .combination(combination)
                        .starNumber(combinationStarRateRequestDto2.getStarNumber())
                        .build()
        );

        combinationStarRateService.createCombinationStarRate(combination.getId(), member, combinationStarRateRequestDto1);

        //then
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.createCombinationStarRate(combination.getId(), member, combinationStarRateRequestDto2));
    }

    @Test
    @DisplayName("영양제 조합 별점 수정")
    public void 영양제_조합_별점_수정(){
        //given
        CombinationStarRateRequestDto combinationStarRateRequestDto1 = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());
        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .member(member)
                .starNumber(combinationStarRateRequestDto1.getStarNumber())
                .build();
        given(combinationStarRateRepository.save(any())).willReturn(insert);
        combinationStarRateService.createCombinationStarRate(combination.getId(), member, combinationStarRateRequestDto1);

        //when
        assertEquals(combination.getRating(), 2);
        combination.getCombinationStarRates().set(0, insert);
        doReturn(Optional.ofNullable(insert))
                .when(combinationStarRateRepository).findIdAndMemberAndComb(insert.getId(), member, combination.getId());


        CombinationStarRateRequestDto combinationStarRateRequestDto2 = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(4).build());
        combinationStarRateService.updateCombinationStarRate(combination.getId(), insert.getId(), member, combinationStarRateRequestDto2);

        assertEquals(combination.getRating(),4);
    }

    @Test
    @DisplayName("영양제 조합 별점 수정 오류(영양제 조합 존재X)")
    public void 영양제_조합_별점_수정_영양제_조합_존재X(){
        CombinationStarRateRequestDto combinationStarRateRequestDto = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());

        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.updateCombinationStarRate(2L,1L, member, combinationStarRateRequestDto),
                "2의 영양제 조합이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("영양제 조합 별점 수정 오류(영양제 조합 별점 존재X)")
    public void 영양제_조합_별점_수정_영양제_조합_별점_존재X(){
        CombinationStarRateRequestDto combinationStarRateRequestDto = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());

        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.updateCombinationStarRate(combination.getId(),1L, member, combinationStarRateRequestDto));
    }

    @Test
    @DisplayName("영양제 조합 별점 조회")
    public void 영양제_조합_별점_조회(){
        //given
        CombinationStarRateRequestDto combinationStarRateRequestDto = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());

        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .starNumber(combinationStarRateRequestDto.getStarNumber())
                .build();
        given(combinationStarRateRepository.save(any())).willReturn(insert);

        given(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(), member)).willReturn(Optional.ofNullable(insert));

        //when
        CombinationStarRateResponseDto combinationStarRateResponseDto = combinationStarRateService.getCombinationStarRateByCombination(combination.getId(), member);

        //then
        assertEquals(combinationStarRateResponseDto.getId(), 1L);
        assertEquals(combinationStarRateResponseDto.getStarNumber(), combinationStarRateRequestDto.getStarNumber());
    }

    @Test
    @DisplayName("영양제 조합 별점 삭제")
    public void 영양제_조합_별점_삭제(){
        //given
        CombinationStarRateRequestDto combinationStarRateRequestDto = new CombinationStarRateRequestDto(CombinationStarRateRequest.builder().starNumber(2).build());
        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .member(member)
                .starNumber(combinationStarRateRequestDto.getStarNumber())
                .build();
        given(combinationStarRateRepository.save(any())).willReturn(insert);
        given(combinationStarRateRepository.findById(any())).willReturn(Optional.ofNullable(insert));
        combinationStarRateService.createCombinationStarRate(combination.getId(),member,combinationStarRateRequestDto);
        //when
        combinationStarRateService.deleteCombinationStarRate(insert.getId(), member);
        CombinationStarRateResponseDto combinationStarRateResponseDto = combinationStarRateService.getCombinationStarRateByCombination(combination.getId(), member);
        //then
        assertNull(combinationStarRateResponseDto.getId());
        assertNull(combinationStarRateResponseDto.getStarNumber());

    }
}