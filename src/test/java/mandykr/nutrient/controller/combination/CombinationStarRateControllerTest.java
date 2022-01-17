package mandykr.nutrient.controller.combination;

import mandykr.nutrient.controller.supplement.SupplementReplyController;
import mandykr.nutrient.dto.combination.starRate.CombinationStarRateRequestDto;
import mandykr.nutrient.dto.combination.starRate.CombinationStarRateResponseDto;
import mandykr.nutrient.dto.supplement.reply.SupplementReplyRequestDto;
import mandykr.nutrient.dto.supplement.reply.SupplementReplyResponseDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.supplement.SupplementReply;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.combination.CombinationStarRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CombinationStarRateController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CombinationStarRateControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CombinationStarRateService combinationStarRateService;

    @MockBean
    MemberRepository memberRepository;

    Member saveMember;
    Combination saveCombination;

    @BeforeEach
    public void setup(){
        this.saveCombination = Combination.builder()
                .id(1L)
                .rating(0.0)
                .caption("영양제조합1")
                .build();

        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testMemberId1");
        member.setName("KIM");
        this.saveMember = member;
    }

    @Test
    @DisplayName("내가 등록한 별점 보기(데이터 있음)")
    public void 등록한_별점_조회() throws Exception {
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(2)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.getCombinationStarRateByCombination(anyLong(), any(Member.class))).willReturn(combinationStarRateResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                get("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("getCombinationStarRateByCombination"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(1)))
                .andExpect(jsonPath("$.response.starNumber", is(2)));
    }


    @Test
    @DisplayName("내가 등록한 별점 보기(데이터 없음)")
    public void 등록한_별점_조회_데이터없음() throws Exception {
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.NULL
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.getCombinationStarRateByCombination(anyLong(), any(Member.class))).willReturn(combinationStarRateResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                get("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("getCombinationStarRateByCombination"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(nullValue())))
                .andExpect(jsonPath("$.response.starNumber", is(nullValue())));
    }

    
    

    @Test
    @DisplayName("영양제 조합 별점 등록")
    public void 영양제_조합_별점_등록() throws Exception {
        //given
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(2)
                                .member(saveMember)
                                .combination(saveCombination)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.createCombinationStarRate(anyLong(), any(Member.class), any(CombinationStarRateRequestDto.class)))
                .willReturn(combinationStarRateResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                post("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 2}")
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("createCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(1)))
                .andExpect(jsonPath("$.response.starNumber", is(2)));
    }

    /*
    @Test
    @DisplayName("영양제 조합 별점 등록(별점이 5초과인 경우)")
    public void 영양제_조합_별점_등록_별점이_5초과() throws Exception {
        //given
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(6)
                                .member(saveMember)
                                .combination(saveCombination)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.createCombinationStarRate(anyLong(), any(Member.class), any(CombinationStarRateRequestDto.class)))
                .willReturn(combinationStarRateResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                post("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 6}")
        );

        //then
        //validation 에러 어떻게 잡을지??
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("createCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(saveCombination.getId()+"의 영양제 조합이 존재하지 않습니다.")));
    }
     */

    @Test
    @DisplayName("영양제 조합 별점 등록(영양제 조합X 에러)")
    public void 영양제_조합_별점_등록_영양제_조합X() throws Exception {
        //given
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(2)
                                .member(saveMember)
                                .combination(saveCombination)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.createCombinationStarRate(anyLong(), any(Member.class), any(CombinationStarRateRequestDto.class)))
                .willThrow(new IllegalArgumentException(saveCombination.getId()+"의 영양제 조합이 존재하지 않습니다."));

        //when
        ResultActions result = mockMvc.perform(
                post("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 2}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("createCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(saveCombination.getId()+"의 영양제 조합이 존재하지 않습니다.")));
    }

    @Test
    @DisplayName("영양제 조합 별점 등록(영양제 조합 별점 존재)")
    public void 영양제_조합_별점_등록_영양제_조합_별점_존재() throws Exception {
        //given
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(2)
                                .member(saveMember)
                                .combination(saveCombination)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.createCombinationStarRate(anyLong(), any(Member.class), any(CombinationStarRateRequestDto.class)))
                .willThrow(new IllegalArgumentException(saveCombination.getId()+"의 영양제 조합 별점이 존재합니다"));

        //when
        ResultActions result = mockMvc.perform(
                post("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 2}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("createCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(saveCombination.getId()+"의 영양제 조합 별점이 존재합니다")));
    }


    @Test
    @DisplayName("영양제 조합 별점 수정")
    public void 영양제_조합_별점_수정() throws Exception {
        //given
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(4)
                                .member(saveMember)
                                .combination(saveCombination)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.updateCombinationStarRate(anyLong(), anyLong(), any(Member.class), any(CombinationStarRateRequestDto.class)))
                .willReturn(combinationStarRateResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                put("/combination-star-rate/1/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 4}")
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("updateCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(1)))
                .andExpect(jsonPath("$.response.starNumber", is(4)));
    }


    @Test
    @DisplayName("영양제 조합 별점 수정(영양제 조합X)")
    public void 영양제_조합_별점_수정_영양제_조합X() throws Exception {
        //given
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(4)
                                .member(saveMember)
                                .combination(saveCombination)
                                .build()
                );

        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.updateCombinationStarRate(anyLong(), anyLong(), any(Member.class), any(CombinationStarRateRequestDto.class)))
                .willThrow(new IllegalArgumentException(saveCombination.getId()+"의 영양제 조합이 존재하지 않습니다."));

        //when
        ResultActions result = mockMvc.perform(
                put("/combination-star-rate/1/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 4}")
        );


        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("updateCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(saveCombination.getId()+"의 영양제 조합이 존재하지 않습니다.")));
    }

    @Test
    @DisplayName("영양제 조합 별점 수정(영양제 조합 별점 존재X)")
    public void 영양제_조합_별점_수정_영양제_조합_별점X() throws Exception {
        //given
        CombinationStarRateResponseDto combinationStarRateResponseDto =
                new CombinationStarRateResponseDto(
                        CombinationStarRate.builder()
                                .id(1L)
                                .starNumber(4)
                                .member(saveMember)
                                .combination(saveCombination)
                                .build()
                );

        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.updateCombinationStarRate(anyLong(), anyLong(), any(Member.class), any(CombinationStarRateRequestDto.class)))
                .willThrow(new IllegalArgumentException(combinationStarRateResponseDto.getId()+"의 영양제 조합 별점이 존재하지 않습니다."));

        //when
        ResultActions result = mockMvc.perform(
                put("/combination-star-rate/1/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 4}")
        );


        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("updateCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(combinationStarRateResponseDto.getId()+"의 영양제 조합 별점이 존재하지 않습니다.")));
    }

    @Test
    @DisplayName("영양제 조합 별점 삭제")
    public void 영양제_조합_별점_삭제() throws Exception {
        //given
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.deleteCombinationStarRate(anyLong(), any(Member.class))).willReturn(true);

        //when
        ResultActions result = mockMvc.perform(
                delete("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("deleteCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response", is(true)));
    }


    @Test
    @DisplayName("영양제 조합 별점 삭제(영양제 조합 별점 존재 X)")
    public void 영양제_조합_별점_삭제_영양제_조합_별점X() throws Exception {
        //given
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.deleteCombinationStarRate(anyLong(), any(Member.class)))
                .willThrow(new IllegalArgumentException(1L + "의 영양제 조합 별점이 존재하지 않습니다."));


        //when
        ResultActions result = mockMvc.perform(
                delete("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("deleteCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(1L+"의 영양제 조합 별점이 존재하지 않습니다.")));
    }


    @Test
    @DisplayName("영양제 조합 별점 삭제(영양제 조합 별점 존재X, 내께 아니다.)")
    public void 영양제_조합_별점_삭제_영양제_조합_별점_내것이_아님() throws Exception {
        //given
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(combinationStarRateService.deleteCombinationStarRate(anyLong(), any(Member.class)))
                .willThrow(new IllegalArgumentException(saveMember.getMemberId() + "의 영양제 조합 별점이 존재하지 않습니다."));


        //when
        ResultActions result = mockMvc.perform(
                delete("/combination-star-rate/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("deleteCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(saveMember.getMemberId() + "의 영양제 조합 별점이 존재하지 않습니다.")));
    }
}