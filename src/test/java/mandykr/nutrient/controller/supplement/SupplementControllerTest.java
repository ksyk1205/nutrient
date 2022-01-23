package mandykr.nutrient.controller.supplement;

import mandykr.nutrient.dto.supplement.*;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import mandykr.nutrient.service.supplement.SupplementService;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplementController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SupplementControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    SupplementService supplementService;

    Supplement supplement1;
    Supplement supplement2;
    SupplementCategory parentCategory;
    SupplementCategory category;
    List<SupplementSearchResponse> supplementDtoList = new ArrayList<>();

    @BeforeEach
    void beforeEach(){

        parentCategory = SupplementCategory.builder().name("오메가369/피쉬오일").depth(0).build();
        category = SupplementCategory.builder().id(1L).name("오메가3").depth(1).parentCategory(parentCategory).build();


        supplement1 = Supplement.builder()
                .id(1L)
                .name("testSupplement1")
                .prdlstReportNo("111-123")
                .supplementCategory(category)
                .ranking(0.0)
                .deleteFlag(false).build();
        supplement2 = Supplement.builder()
                .id(2L)
                .name("testSupplement2")
                .prdlstReportNo("111-456")
                .ranking(0.0)
                .deleteFlag(false).build();


        supplementDtoList.add((new SupplementSearchResponse(supplement1.getId(),
                supplement1.getName(),
                supplement1.getPrdlstReportNo(),
                supplement1.getRanking(),
                supplement1.isDeleteFlag(),
                new SupplementSearchResponse.SupplementCategoryDto(category.getId(),
                        category.getName()))));
        supplementDtoList.add(new SupplementSearchResponse(supplement2.getId(),
                supplement2.getName(),
                supplement2.getPrdlstReportNo(),
                supplement2.getRanking(),
                supplement2.isDeleteFlag(),
                new SupplementSearchResponse.SupplementCategoryDto(category.getId(),
                        category.getName())));
    }

    @Test
    void 영양제_전체_조회() throws Exception{
        //given

        //when
        when(supplementService.getSupplementList(any(SupplementSearch.class))).thenReturn(supplementDtoList);
        ResultActions perform = mockMvc.perform(get("/api/supplement?categoryId=&supplementName=")
                .accept(MediaType.APPLICATION_JSON));

        //then
        extracted(supplementDtoList, perform);

    }

    @Test
    @DisplayName("카테고리별 영양제 조회")
    void 영양제_카테고리_조회() throws  Exception{
        //given


        //when
        when(supplementService.getSupplementList(any(SupplementSearch.class))).thenReturn(supplementDtoList);
        ResultActions perform = mockMvc.perform(get("/api/supplement?categoryId=1&supplementName=")
                .accept(MediaType.APPLICATION_JSON));

        //then

        extracted(supplementDtoList, perform);

    }

    @Test
    @DisplayName("영양제 이름으로 조회")
    void 영양제이름_조회() throws  Exception{
        //given


        //when
        when(supplementService.getSupplementList(any(SupplementSearch.class))).thenReturn(supplementDtoList);
        ResultActions perform = mockMvc.perform(get("/api/supplement?categoryId=&supplementName=test")
                .accept(MediaType.APPLICATION_JSON));

        //then
        extracted(supplementDtoList, perform);

    }
    private void extracted(List<SupplementSearchResponse> supplementDtoList, ResultActions perform) throws Exception {
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("getSupplementList"))
                .andExpect(jsonPath("$.success",is(true)))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()", is(supplementDtoList.size())));
        for(int i = 0; i < supplementDtoList.size(); ++i){
            perform.andDo(print())
                    .andExpect(jsonPath("$.response[" + i + "].id", is(supplementDtoList.get(i).getId().intValue())))
                    .andExpect(jsonPath("$.response[" + i + "].name", is(supplementDtoList.get(i).getName())))

                    .andExpect(jsonPath("$.response[" + i + "].ranking", is(supplementDtoList.get(i).getRanking())))
                    .andExpect(jsonPath("$.response[" + i + "].prdlstReportNo", is(supplementDtoList.get(i).getPrdlstReportNo())))
                    .andExpect(jsonPath("$.response[" + i + "].supplementCategoryDto.id", is(supplementDtoList.get(i).getSupplementCategoryDto().getId().intValue())))
                    .andExpect(jsonPath("$.response[" + i + "].supplementCategoryDto.name", is(supplementDtoList.get(i).getSupplementCategoryDto().getName())));
        }
    }

    @Test
    void 영양제_등록() throws Exception{
        //given
        SupplementRequest supplementRequest = new SupplementRequest(supplement1.getName(), supplement1.getPrdlstReportNo());
        //when
        when(supplementService.createSupplement(new SupplementRequestDto(supplementRequest),category.getId()))
                .thenReturn(new SupplementResponseDto(supplement1));
        ResultActions perform = mockMvc.perform(post("/api/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testSupplement1\" " +
                        ",\"prdlstReportNo\": \"111-123\"}" ));

        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("createSupplement"))
                .andExpect(jsonPath("$.success",is(true)))
                .andExpect(jsonPath("$.response.id", is(supplement1.getId().intValue())))
                .andExpect(jsonPath("$.response.name", is(supplement1.getName())))
                .andExpect(jsonPath("$.response.prdlstReportNo", is(supplement1.getPrdlstReportNo())));

    }

    @Test
    void 영양제_수정() throws Exception{
        //given
        Supplement updatesupplement = Supplement.builder()
                .id(3L)
                .name("testSupplement3")
                .prdlstReportNo("111-333")
                .supplementCategory(category)
                .ranking(0.0)
                .deleteFlag(false).build();
        SupplementRequest supplementRequest = new SupplementRequest(updatesupplement.getName(), updatesupplement.getPrdlstReportNo());

        //when
        when(supplementService.updateSupplement(category.getId(), updatesupplement.getId(), new SupplementRequestDto(supplementRequest)))
                .thenReturn(new SupplementResponseDto(updatesupplement));
        ResultActions perform = mockMvc.perform(put("/api/1/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"testSupplement3\"," +
                                "\"prdlstReportNo\" :\"111-333\"}"));
        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("updateSupplement"))
                .andExpect(jsonPath("$.success",is(true)))
                .andExpect(jsonPath("$.response.id", is(updatesupplement.getId().intValue())))
                .andExpect(jsonPath("$.response.name", is(updatesupplement.getName())))
                .andExpect(jsonPath("$.response.prdlstReportNo", is(updatesupplement.getPrdlstReportNo())));

    }

    @Test
    void 영양제_삭제() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(delete("/api/1").accept(MediaType.APPLICATION_JSON));
        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("deleteSupplement"));
    }

    @Test
    void 영양제_단건_조회() throws Exception{
        //given

        //when
        when(supplementService.getSupplement(supplement1.getId())).thenReturn(new SupplementResponseDto(supplement1));
        ResultActions perform = mockMvc.perform(get("/api/1").accept(MediaType.APPLICATION_JSON));

        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("getSupplement"))
                .andExpect(jsonPath("$.success",is(true)))
                .andExpect(jsonPath("$.response.id", is(supplement1.getId().intValue())))
                .andExpect(jsonPath("$.response.name", is(supplement1.getName())))
                .andExpect(jsonPath("$.response.ranking", is(supplement1.getRanking())))
                .andExpect(jsonPath("$.response.prdlstReportNo", is(supplement1.getPrdlstReportNo())));

    }
}