package mandykr.nutrient.controller.combination;

import com.fasterxml.jackson.databind.ObjectMapper;
import mandykr.nutrient.dto.combination.CombinationCreateRequest;
import mandykr.nutrient.dto.combination.CombinationDetailDto;
import mandykr.nutrient.dto.combination.CombinationUpdateRequest;
import mandykr.nutrient.service.combination.CombinationService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CombinationController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CombinationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CombinationService combinationService;

    String caption = "caption";
    List<Long> supplementIds;
    List<CombinationDetailDto.SupplementDto> supplementDtoes = new ArrayList<>();
    CombinationDetailDto combinationDetailDto;

    @BeforeEach
    public void setup(){
        supplementIds = LongStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());
        CombinationDetailDto.SupplementDto supplementDto1 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(0)).name("영양제1").categoryId(1L).categoryName("비타민A").build();
        supplementDtoes.add(supplementDto1);
        CombinationDetailDto.SupplementDto supplementDto2 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(1)).name("영양제2").categoryId(2L).categoryName("비타민B").build();
        supplementDtoes.add(supplementDto2);
        CombinationDetailDto.SupplementDto supplementDto3 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(2)).name("영양제3").categoryId(3L).categoryName("비타민C").build();
        supplementDtoes.add(supplementDto3);
        combinationDetailDto = CombinationDetailDto.builder()
                .id(1L)
                .caption(caption)
                .rating(0.0)
                .supplementDtoList(supplementDtoes)
                .build();
    }

    @Test
    @DisplayName("영양제 조합 등록")
    public void 영양제_조합_등록() throws Exception {
        //given
        List<Long> supplementIds = Arrays.asList(1L, 2L, 3L);
        CombinationCreateRequest combinationCreateRequest = new CombinationCreateRequest("TEST", supplementIds);

        List<CombinationDetailDto.SupplementDto> supplementDtoes = new ArrayList<>();
        CombinationDetailDto.SupplementDto supplementDto1 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(0)).name("영양제1").categoryId(1L).categoryName("비타민A").build();
        supplementDtoes.add(supplementDto1);
        CombinationDetailDto.SupplementDto supplementDto2 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(1)).name("영양제2").categoryId(2L).categoryName("비타민B").build();
        supplementDtoes.add(supplementDto2);
        CombinationDetailDto.SupplementDto supplementDto3 = CombinationDetailDto.SupplementDto.builder().id(supplementIds.get(2)).name("영양제3").categoryId(3L).categoryName("비타민C").build();
        supplementDtoes.add(supplementDto3);
        CombinationDetailDto combinationDetailDto = CombinationDetailDto.builder()
                .id(1L)
                .caption("TEST")
                .rating(0.0)
                .supplementDtoList(supplementDtoes)
                .build();

        //when
        when(combinationService.createCombination(combinationCreateRequest))
                .thenReturn(combinationDetailDto);

        ResultActions result = mockMvc.perform(
                post("/api/combinations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinationCreateRequest)))
                ;
                

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CombinationController.class))
                .andExpect(handler().methodName("createCombination"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(combinationDetailDto.getId().intValue())))
                .andExpect(jsonPath("$.response.caption", is(combinationDetailDto.getCaption())))
                .andExpect(jsonPath("$.response.rating", is(combinationDetailDto.getRating())));
        List<CombinationDetailDto.SupplementDto> supplementDtoList = combinationDetailDto.getSupplementDtoList();
        for(int i=0; i< supplementDtoList.size(); ++i) {
            result
                .andExpect(jsonPath("$.response.supplementDtoList[" + i + "].id", is(supplementDtoList.get(i).getId().intValue())))
                .andExpect(jsonPath("$.response.supplementDtoList[" + i + "].name", is(supplementDtoList.get(i).getName())))
                .andExpect(jsonPath("$.response.supplementDtoList[" + i + "].categoryId", is(supplementDtoList.get(i).getCategoryId().intValue())))
                .andExpect(jsonPath("$.response.supplementDtoList[" + i + "].categoryName", is(supplementDtoList.get(i).getCategoryName())));
        }
    }

    @Test
    @DisplayName("영양제 조합 등록(Caption 비어 있음)")
    public void 영양제_조합_등록_실패_비어있음() throws Exception {
        List<Long> supplementIds = Arrays.asList(1L, 2L, 3L);
        CombinationCreateRequest combinationCreateRequest = new CombinationCreateRequest("", supplementIds);

        ResultActions result = mockMvc.perform(
                post("/api/combinations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinationCreateRequest)))
                ;
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationController.class))
                .andExpect(handler().methodName("createCombination"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("must not be empty")));

    }

    @Test
    @DisplayName("영양제 조합 등록(Caption 길이 초과)")
    public void 영양제_조합_등록_실패_Caption_길이초과() throws Exception {
        List<Long> supplementIds = Arrays.asList(1L, 2L, 3L);
        CombinationCreateRequest combinationCreateRequest = new CombinationCreateRequest("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", supplementIds);

        ResultActions result = mockMvc.perform(
                post("/api/combinations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinationCreateRequest)))
                ;
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationController.class))
                .andExpect(handler().methodName("createCombination"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("size must be between 1 and 50")));

    }

    @Test
    @DisplayName("영양제 조합 등록(영양제 List 비어 있음)")
    public void 영양제_조합_등록_실패_영양제_비어있음() throws Exception {
        List<Long> supplementIds = new ArrayList<>();
        CombinationCreateRequest combinationCreateRequest = new CombinationCreateRequest("TEST", supplementIds);

        ResultActions result = mockMvc.perform(
                post("/api/combinations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinationCreateRequest)))
                ;
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationController.class))
                .andExpect(handler().methodName("createCombination"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("size must be between 1 and 10")));

    }
    @Test
    @DisplayName("영양제 조합 등록(영양제 List 10개 초과)")
    public void 영양제_조합_등록_실패_영양제_10개초과() throws Exception {
        List<Long> supplementIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
        CombinationCreateRequest combinationCreateRequest = new CombinationCreateRequest("TEST", supplementIds);

        ResultActions result = mockMvc.perform(
                post("/api/combinations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinationCreateRequest)))
                ;
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(CombinationController.class))
                .andExpect(handler().methodName("createCombination"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("size must be between 1 and 10")));

    }

    @Test
    @DisplayName("영양제 조합 내용과 영양제 목록으로 수정을 요청하면 수정된 영양제 조합을 반환한다")
    void 영양제_조합_수정() throws Exception {
        // given
        CombinationUpdateRequest request = new CombinationUpdateRequest(caption, supplementIds);
        given(combinationService.updateCombination(combinationDetailDto.getId(), request))
                .willReturn(combinationDetailDto);

        // when
        ResultActions result = 영양제_조합_수정_요청(combinationDetailDto.getId(), request);

        // then
        영양제_조합_수정_완료(result);

    }

    private ResultActions 영양제_조합_수정_요청(Long id, CombinationUpdateRequest request) throws Exception {
        return  mockMvc.perform(
                put("/api/combination/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)));
    }

    private void 영양제_조합_수정_완료(ResultActions result) throws Exception {
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.caption", is(combinationDetailDto.getCaption())))
                .andExpect(jsonPath("$.response.supplementDtoList.*",
                        hasSize(combinationDetailDto.getSupplementDtoList().size())));
    }

    @Test
    @DisplayName("영양제 조합 ID로 영양제를 삭제한다")
    void 영양제_조합_삭제() throws Exception {
        // given
        Long id = 1L;
        // when
        ResultActions result = mockMvc.perform(
            delete("/api/combination/{id}", id)
            .accept(MediaType.APPLICATION_JSON));
        // then

    }
}