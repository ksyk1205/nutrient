package mandykr.nutrient.controller.supplement;

import mandykr.nutrient.config.SecurityConfig;
import mandykr.nutrient.dto.supplement.*;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.security.WithMockJwtAuthentication;
import mandykr.nutrient.service.supplement.SupplementService;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.context.annotation.FilterType.ANNOTATION;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SupplementController.class,
        excludeFilters = { //!Added!
                @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = SecurityConfig.class) })
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
    Pageable pageable;
    Page<SupplementSearchResponse> supplementSearchResponses;

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
        pageable = new PageRequestUtil(1,1).getPageable();
        supplementSearchResponses = new PageImpl<>(supplementDtoList, pageable, supplementDtoList.size());
    }

    @Test
    @WithMockUser
    void 영양제_전체_조회() throws Exception{
        //given

        //when
        when(supplementService.getSupplementList(any(SupplementSearchRequest.class), any(PageRequest.class))).thenReturn(supplementSearchResponses);
        ResultActions perform = mockMvc.perform(get("/api/supplement?categoryId=&supplementName=")
                .params(toMultiValueMap(pageable))
                .accept(MediaType.APPLICATION_JSON));

        //then
        extracted(supplementSearchResponses, perform);

    }

    @Test
    @DisplayName("카테고리별 영양제 조회")
    @WithMockUser
    void 영양제_카테고리_조회() throws Exception{
        //given

        //when
        when(supplementService.getSupplementList(any(SupplementSearchRequest.class), any(PageRequest.class))).thenReturn(supplementSearchResponses);
        ResultActions perform = mockMvc.perform(get("/api/supplement?categoryId=1&supplementName=")
                .params(toMultiValueMap(pageable))
                .accept(MediaType.APPLICATION_JSON));

        //then
        extracted(supplementSearchResponses, perform);

    }

    @Test
    @DisplayName("영양제 이름으로 조회")
    @WithMockUser
    void 영양제이름_조회() throws Exception{
        //given

        //when
        when(supplementService.getSupplementList(any(SupplementSearchRequest.class), any(PageRequest.class))).thenReturn(supplementSearchResponses);
        ResultActions perform = mockMvc.perform(get("/api/supplement?categoryId=&supplementName='test'")
                .params(toMultiValueMap(pageable))
                .accept(MediaType.APPLICATION_JSON));

        //then
        extracted(supplementSearchResponses, perform);

    }

    private MultiValueMap<String, String> toMultiValueMap(Pageable pageable) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("page", String.valueOf(pageable.getPageNumber()));
        map.add("size", String.valueOf(pageable.getPageSize()));
        return map;
    }

    private void extracted(Page<SupplementSearchResponse> supplementSearchResponses, ResultActions perform) throws Exception {
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("getSupplementList"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.size", is(supplementSearchResponses.getSize())));
        for(int i = 0; i < supplementSearchResponses.getSize(); ++i){
            perform.andDo(print())
                    .andExpect(jsonPath("$.response.content[" + i + "].id", is(supplementSearchResponses.getContent().get(i).getId().intValue())))
                    .andExpect(jsonPath("$.response.content[" + i + "].name", is(supplementSearchResponses.getContent().get(i).getName())))
                    .andExpect(jsonPath("$.response.content[" + i + "].ranking", is(supplementSearchResponses.getContent().get(i).getRanking())))
                    .andExpect(jsonPath("$.response.content[" + i + "].prdlstReportNo", is(supplementSearchResponses.getContent().get(i).getPrdlstReportNo())))
                    .andExpect(jsonPath("$.response.content[" + i + "].supplementCategoryDto.id", is(supplementSearchResponses.getContent().get(i).getSupplementCategoryDto().getId().intValue())))
                    .andExpect(jsonPath("$.response.content[" + i + "].supplementCategoryDto.name", is(supplementSearchResponses.getContent().get(i).getSupplementCategoryDto().getName())));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 영양제_등록() throws Exception{
        //given
        SupplementRequest supplementRequest = new SupplementRequest(supplement1.getName(), supplement1.getPrdlstReportNo());
        //when
        when(supplementService.createSupplement(supplementRequest, category.getId()))
                .thenReturn(new SupplementResponse(supplement1));
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
    @WithMockUser(roles = "ADMIN")
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
        when(supplementService.updateSupplement(category.getId(), updatesupplement.getId(), supplementRequest))
                .thenReturn(new SupplementResponse(updatesupplement));
        ResultActions perform = mockMvc.perform(put("/api/1/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"testSupplement3\"," +
                                "\"prdlstReportNo\" :\"111-333\"}"));
        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("updateSupplement"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(updatesupplement.getId().intValue())))
                .andExpect(jsonPath("$.response.name", is(updatesupplement.getName())))
                .andExpect(jsonPath("$.response.prdlstReportNo", is(updatesupplement.getPrdlstReportNo())));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser
    void 영양제_단건_조회() throws Exception{
        //given

        //when
        when(supplementService.getSupplement(supplement1.getId())).thenReturn(new SupplementResponse(supplement1));
        ResultActions perform = mockMvc.perform(get("/api/1").accept(MediaType.APPLICATION_JSON));

        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("getSupplement"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(supplement1.getId().intValue())))
                .andExpect(jsonPath("$.response.name", is(supplement1.getName())))
                .andExpect(jsonPath("$.response.ranking", is(supplement1.getRanking())))
                .andExpect(jsonPath("$.response.prdlstReportNo", is(supplement1.getPrdlstReportNo())));

    }

    @Test
    @WithMockUser
    void 영양제_콤보_조회() throws Exception{
        //given
        List<SupplementSearchComboResponse> supplementSearchComboResponses = new ArrayList<>();
        supplementSearchComboResponses.add(new SupplementSearchComboResponse(supplement1.getId(), supplement1.getName()));
        supplementSearchComboResponses.add(new SupplementSearchComboResponse(supplement2.getId(), supplement2.getName()));

        //when
        when(supplementService.getSupplementSearchCombo(any(SupplementSearchComboRequest.class))).thenReturn(supplementSearchComboResponses);
        ResultActions perform = mockMvc.perform(get("/api/supplement/combo?name='test'")
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementController.class))
                .andExpect(handler().methodName("getSupplementSearchCombo"))
                .andExpect(jsonPath("$.success",is(true)));
                for(int i = 0; i < supplementSearchComboResponses.size(); ++i) {
                    perform.andExpect(jsonPath("$.response[" + i + "].id", is(supplementSearchComboResponses.get(i).getId().intValue())))
                            .andExpect(jsonPath("$.response[" + i + "].name", is(supplementSearchComboResponses.get(i).getName())));
                }
    }
}