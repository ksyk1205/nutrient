package mandykr.nutrient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.service.SupplementCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplementCategoryController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SupplementCategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SupplementCategoryService categoryService;

    @Autowired
    private WebApplicationContext ctx;

//    @BeforeEach
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))
//                .build();
//    }

    @Test
    void 카테고리_리스트를_조회한다() throws Exception {
        // given
        List<SupplementCategory> categoryList = new ArrayList<>();
        categoryList.add(
                SupplementCategory.toEntity(1L,"카테고리1", 1,
                        SupplementCategory.toEntity(2L, "카테고리2", 0)));
        categoryList.add(
                SupplementCategory.toEntity(3L,"카테고리3", 1,
                        SupplementCategory.toEntity(4L, "카테고리4", 0)));

        given(categoryService.getCategoryList()).willReturn(categoryList);

        // when
        ResultActions perform = mockMvc.perform(get("/api/categories"));

        // then
        then(categoryService).should(times(1)).getCategoryList();
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.[0].id", is(1)))
                .andExpect(jsonPath("$.response.[1].id", is(3)));
    }

    @Test
    void 카테고리_리스트를_수정한다() throws Exception {
        // given
        List<SupplementCategoryDto> categoryDtoList = new ArrayList<>();
        categoryDtoList.add(
                SupplementCategoryDto.toCategoryDto(1L, "카테고리1", 1, 2L));

        List<SupplementCategory> categoryList = new ArrayList<>();
        categoryList.add(
                SupplementCategory.toEntity(1L,"카테고리1", 1,
                        SupplementCategory.toEntity(2L, "카테고리2", 0)));
        categoryList.add(
                SupplementCategory.toEntity(3L,"카테고리3", 1,
                        SupplementCategory.toEntity(4L, "카테고리4", 0)));

        given(categoryService.getCategoryList()).willReturn(categoryList);

        // when
        ResultActions perform = mockMvc.perform(
                put("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDtoList)));

        // then
        then(categoryService).should(times(1)).updateCategories(any(List.class));
        then(categoryService).should(times(1)).getCategoryList();
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.[0].id", is(1)))
                .andExpect(jsonPath("$.response.[1].id", is(3)));

    }

    @Test
    void 카테고리_리스트를_저장하거나_수정한다() throws Exception {
        // given
        List<SupplementCategoryDto> categoryDtoList = new ArrayList<>();
        categoryDtoList.add(
                SupplementCategoryDto.toCategoryDto(1L, "카테고리1", 1, 2L));
        categoryDtoList.add(
                SupplementCategoryDto.toCategoryDto(0L, "카테고리2", 1, 1L));

        List<SupplementCategory> categoryList = new ArrayList<>();
        categoryList.add(
                SupplementCategory.toEntity(1L,"카테고리1", 1,
                        SupplementCategory.toEntity(2L, "카테고리2", 0)));
        categoryList.add(
                SupplementCategory.toEntity(3L,"카테고리3", 1,
                        SupplementCategory.toEntity(4L, "카테고리4", 0)));

        given(categoryService.getCategoryList()).willReturn(categoryList);

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDtoList)));

        // then
        then(categoryService).should(times(1)).createAndUpdateCategories(any(List.class));
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.[0].id", is(1)))
                .andExpect(jsonPath("$.response.[1].id", is(3)));

    }
}