package mandykr.nutrient.service;

import mandykr.nutrient.dto.request.CombineStarRateRequest;
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
        member.setMemberId("testMember");
        member.setName("KIM");
        this.member = member;

        given(combinationRepository.findByIdFetch(combination.getId())).willReturn(Optional.ofNullable(combination));
    }

    @Test
    public void 조합_별점_등록_테스트(){
        CombineStarRateRequest request = new CombineStarRateRequest();
        request.setStarNumber(2);

        combinationStarRateService.saveCombinationStarRate(combination.getId(), member, request);

        Assertions.assertEquals(combination.getRating(),2);
    }

    @Test
    public void 등록_영양제_존재X_테스트(){
        CombineStarRateRequest request = new CombineStarRateRequest();
        request.setStarNumber(2);
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.saveCombinationStarRate(2L, member, request),
                "2의 영양제 조합이 존재하지 않습니다.");
    }

    @Test
    public void 등록_영양제_별점_존재_테스트(){
        CombineStarRateRequest request1 = new CombineStarRateRequest();
        request1.setStarNumber(2);
        combinationStarRateService.saveCombinationStarRate(combination.getId(), member, request1);
        CombineStarRateRequest request2 = new CombineStarRateRequest();
        request2.setStarNumber(4);
        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .member(member)
                .starNumber(request1.getStarNumber())
                .build();
        given(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(), member)).willReturn(Optional.ofNullable(insert));
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.saveCombinationStarRate(combination.getId(), member, request2));
    }

    @Test
    public void 조합_별점_업데이트_테스트(){
        CombineStarRateRequest request1 = new CombineStarRateRequest();
        request1.setStarNumber(2);
        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .member(member)
                .starNumber(request1.getStarNumber())
                .build();
        given(combinationStarRateRepository.save(any())).willReturn(insert);
        combinationStarRateService.saveCombinationStarRate(combination.getId(), member, request1);
        Assertions.assertEquals(combination.getRating(), 2);
        combination.getCombinationStarRates().set(0, insert);
        doReturn(Optional.ofNullable(insert))
                .when(combinationStarRateRepository).findIdAndMemberAndComb(insert.getId(), member, combination.getId());


        request1.setStarNumber(4);
        combinationStarRateService.updateCombinationStarRate(combination.getId(), insert.getId(), member, request1);

        Assertions.assertEquals(combination.getRating(),4);
    }

    @Test
    public void 조합_별점_업데이트_영양제존재X_테스트(){
        CombineStarRateRequest request = new CombineStarRateRequest();
        request.setStarNumber(2);
        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.updateCombinationStarRate(2L,1L, member, request),
                "2의 영양제 조합이 존재하지 않습니다.");
    }

    @Test
    public void 조합_별점_업데이트_영양제별점존재X_테스트(){
        CombineStarRateRequest request = new CombineStarRateRequest();
        request.setStarNumber(2);

        assertThrows(IllegalArgumentException.class,
                ()->combinationStarRateService.updateCombinationStarRate(combination.getId(),1L, member, request));
    }

    @Test
    public void 조합_별점_조회_테스트(){
        CombineStarRateRequest request = new CombineStarRateRequest();
        request.setStarNumber(2);

        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .starNumber(request.getStarNumber())
                .build();
        given(combinationStarRateRepository.save(any())).willReturn(insert);

        given(combinationStarRateRepository.findByCombinationIdAndMember(combination.getId(), member)).willReturn(Optional.ofNullable(insert));
        assertTrue(combinationStarRateService.getCombineStarRateByCombine(combination.getId(), member).isPresent());
    }

    @Test
    public void 조합_별점_삭제_테스트(){
        CombineStarRateRequest request = new CombineStarRateRequest();
        request.setStarNumber(2);
        CombinationStarRate insert = CombinationStarRate.builder()
                .id(1L)
                .member(member)
                .starNumber(request.getStarNumber())
                .build();
        given(combinationStarRateRepository.save(any())).willReturn(insert);
        given(combinationStarRateRepository.findById(any())).willReturn(Optional.ofNullable(insert));
        combinationStarRateService.saveCombinationStarRate(combination.getId(),member,request);

        combinationStarRateService.deleteCombinationStarRate(insert.getId(), member);

        assertFalse(combinationStarRateService.getCombineStarRateByCombine(combination.getId(), member).isPresent());
    }
}