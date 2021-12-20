package mandykr.nutrient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mandykr.nutrient.config.SecurityConfig;
import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.service.SupplementCategoryService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplementCategoryController.class)
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
    void 카테고리_리스트를_수정한다() throws Exception {
        List<SupplementCategoryDto> categoryDtoList = new ArrayList<>();
        categoryDtoList.add(
                new SupplementCategoryDto(1L, "카테고리1", 1,
                        new SupplementCategoryDto(2L, "카테고리2", 0, null)));

        List<SupplementCategory> categoryList = new ArrayList<>();
        categoryList.add(
                new SupplementCategory("카테고리1", 1,
                        new SupplementCategory("카테고리2", 0)));

        given(categoryService.getSupplementList()).willReturn(categoryList);

        ResultActions perform = mockMvc.perform(
                put("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDtoList)));

        perform
                .andExpect(status().isOk());
        then(categoryService).should(times(1)).updateCategoryList(any());
    }
}