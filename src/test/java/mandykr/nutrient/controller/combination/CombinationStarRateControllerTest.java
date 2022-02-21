package mandykr.nutrient.controller.combination;

import mandykr.nutrient.dto.combination.starRate.CombinationStarRateDto;
import mandykr.nutrient.dto.combination.starRate.request.CombinationStarRateRequest;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.repository.member.MemberRepository;
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

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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

    Member member;
    Combination combination;
    CombinationStarRateDto combinationStarRateDto1;
    CombinationStarRateDto combinationStarRateDto2;
    CombinationStarRateDto emptySupCombinationStarRateDto;

    @BeforeEach
    public void setup(){
        member = new Member();
        //member.setId(1L);
        //member.setMemberId("testMemberId1");
        //member.setName("KIM");
        this.combination = Combination.builder()
                .id(1L)
                .rating(0.0)
                .caption("영양제조합1")
                .build();
        combinationStarRateDto1 = make(1L, 2, combination);
        combinationStarRateDto2 = make(2L, 4, combination);
        emptySupCombinationStarRateDto = makeEmpty();
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));

    }

    private CombinationStarRateDto makeEmpty() {
        CombinationStarRateDto combinationStarRateDto
                = new CombinationStarRateDto(CombinationStarRate.builder().build());
        return combinationStarRateDto;
    }

    private CombinationStarRateDto make(long id, int starNumber, Combination combination) {
        CombinationStarRateDto combinationStarRateDto
                = new CombinationStarRateDto(
                        CombinationStarRate.builder()
                                .id(id)
                                .starNumber(starNumber)
                                .combination(combination)
                                .build());
        return combinationStarRateDto;
    }

    @Test
    @DisplayName("내가 등록한 별점 보기(데이터 있음)")
    public void 등록한_별점_조회() throws Exception {
        //given

        //when
        when(combinationStarRateService.getCombinationStarRateByCombination(combinationStarRateDto1.getId(), member)).thenReturn(combinationStarRateDto1);
        ResultActions result = mockMvc.perform(
                get("/api/combination-star-rate/{combinationId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("getCombinationStarRateByCombination"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(combinationStarRateDto1.getId().intValue())))
                .andExpect(jsonPath("$.response.starNumber", is(combinationStarRateDto1.getStarNumber())));
    }


    @Test
    @DisplayName("내가 등록한 별점 보기(데이터 없음)")
    public void 등록한_별점_조회_데이터없음() throws Exception {

        //when
        when(combinationStarRateService.getCombinationStarRateByCombination(combinationStarRateDto1.getId(), member))
                .thenReturn(emptySupCombinationStarRateDto);

        ResultActions result = mockMvc.perform(
                get("/api/combination-star-rate/{combinationId}", "1")
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

        //when
        when(combinationStarRateService.createCombinationStarRate(anyLong(), any(Member.class), any(CombinationStarRateRequest.class)))
                .thenReturn(combinationStarRateDto1);

        ResultActions result = mockMvc.perform(
                post("/api/combination-star-rate/{combinationId}", "1")
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
                .andExpect(jsonPath("$.response.id", is(combinationStarRateDto1.getId().intValue())))
                .andExpect(jsonPath("$.response.starNumber", is(combinationStarRateDto1.getStarNumber())));
    }

    @Test
    @DisplayName("영양제 조합 별점 등록(별점이 5초과인 경우)")
    public void 영양제_조합_별점_등록_별점이_5초과() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(
                post("/api/combination-star-rate/{combinationId}", "1")
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
                .andExpect(jsonPath("$.error.message", is("must be less than or equal to 5")));
    }

    @Test
    @DisplayName("영양제 조합 별점 등록(별점이 0미만인 경우)")
    public void 영양제_조합_별점_등록_별점이_0미만() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(
                post("/api/combination-star-rate/{combinationId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": -1}")
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
                .andExpect(jsonPath("$.error.message", is("must be greater than or equal to 0")));
    }

    @Test
    @DisplayName("영양제 조합 별점 등록(영양제 조합X 에러)")
    public void 영양제_조합_별점_등록_영양제_조합X() throws Exception {
        //given

        //when
        when(combinationStarRateService.createCombinationStarRate(anyLong(), any(Member.class), any(CombinationStarRateRequest.class)))
                .thenThrow(new IllegalArgumentException(combinationStarRateDto1.getId()+"의 영양제 조합이 존재하지 않습니다."));
        ResultActions result = mockMvc.perform(
                post("/api/combination-star-rate/{combinationId}", "1")
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
                .andExpect(jsonPath("$.error.message", is(combinationStarRateDto1.getId()+"의 영양제 조합이 존재하지 않습니다.")));
    }

    @Test
    @DisplayName("영양제 조합 별점 등록(영양제 조합 별점 존재)")
    public void 영양제_조합_별점_등록_영양제_조합_별점_존재() throws Exception {
        //given

        //when
        when(combinationStarRateService.createCombinationStarRate(anyLong(), any(Member.class), any(CombinationStarRateRequest.class)))
                .thenThrow(new IllegalArgumentException(combinationStarRateDto1.getId()+"의 영양제 조합 별점이 존재합니다"));
        ResultActions result = mockMvc.perform(
                post("/api/combination-star-rate/{combinationId}", "1")
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
                .andExpect(jsonPath("$.error.message", is(combinationStarRateDto1.getId()+"의 영양제 조합 별점이 존재합니다")));
    }


    @Test
    @DisplayName("영양제 조합 별점 수정")
    public void 영양제_조합_별점_수정() throws Exception {
        //given


        //when
        when(combinationStarRateService.updateCombinationStarRate(anyLong(), anyLong(), any(Member.class), any(CombinationStarRateRequest.class)))
                .thenReturn(combinationStarRateDto1);

        ResultActions result = mockMvc.perform(
                put("/api/combination-star-rate/{combinationId}/{combinationStarRateId}", "1", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 2}")
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("updateCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(combinationStarRateDto1.getId().intValue())))
                .andExpect(jsonPath("$.response.starNumber", is(combinationStarRateDto1.getStarNumber())));
    }


    @Test
    @DisplayName("영양제 조합 별점 수정(영양제 조합X)")
    public void 영양제_조합_별점_수정_영양제_조합X() throws Exception {
        //given

        //when
        when(combinationStarRateService.updateCombinationStarRate(anyLong(), anyLong(), any(Member.class), any(CombinationStarRateRequest.class)))
                .thenThrow(new IllegalArgumentException(combinationStarRateDto1.getId()+"의 영양제 조합이 존재하지 않습니다."));

        ResultActions result = mockMvc.perform(
                put("/api/combination-star-rate/{combinationId}/{combinationStarRateId}", "1", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 2}")
        );


        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("updateCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(combinationStarRateDto1.getId()+"의 영양제 조합이 존재하지 않습니다.")));
    }

    @Test
    @DisplayName("영양제 조합 별점 수정(영양제 조합 별점 존재X)")
    public void 영양제_조합_별점_수정_영양제_조합_별점X() throws Exception {
        //given

        //when
        when(combinationStarRateService.updateCombinationStarRate(anyLong(), anyLong(), any(Member.class), any(CombinationStarRateRequest.class)))
                .thenThrow(new IllegalArgumentException(combinationStarRateDto1.getId()+"의 영양제 조합 별점이 존재하지 않습니다."));
        ResultActions result = mockMvc.perform(
                put("/api/combination-star-rate/{combinationId}/{combinationStarRateId}", "1", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starNumber\": 2}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationStarRateController.class))
                .andExpect(handler().methodName("updateCombinationStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is(combinationStarRateDto1.getId()+"의 영양제 조합 별점이 존재하지 않습니다.")));
    }

    @Test
    @DisplayName("영양제 조합 별점 삭제")
    public void 영양제_조합_별점_삭제() throws Exception {
        //given

        //when
        when(combinationStarRateService.deleteCombinationStarRate(anyLong(), any(Member.class))).thenReturn(true);
        ResultActions result = mockMvc.perform(
                delete("/api/combination-star-rate/{combinationStarRateId}","1")
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

        //when
        when(combinationStarRateService.deleteCombinationStarRate(anyLong(), any(Member.class)))
                .thenThrow(new IllegalArgumentException(combinationStarRateDto1.getId() + "의 영양제 조합 별점이 존재하지 않습니다."));

        ResultActions result = mockMvc.perform(
                delete("/api/combination-star-rate/{combinationStarRateId}","1")
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
                .andExpect(jsonPath("$.error.message", is(combinationStarRateDto1.getId() + "의 영양제 조합 별점이 존재하지 않습니다.")));
    }

}