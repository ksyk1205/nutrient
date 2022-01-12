package mandykr.nutrient.controller.combination;

import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.service.combination.CombinationReplyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@MockBean(CombinationReplyController.class)
class CombinationReplyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CombinationReplyService replyService;

    @Test
    void getCombinationReplyByCombination() throws Exception {
        // given
        Combination combination = new Combination(1L);
        ArrayList<CombinationReplyDto> replyList = new ArrayList<>();
        replyList.add(CombinationReplyDto.builder().id(1L).content("parent1").orders(1L).combinationId(combination.getId()).build());
        replyList.add(CombinationReplyDto.builder().id(2L).content("parent2").orders(1L).combinationId(combination.getId()).build());
        replyList.add(CombinationReplyDto.builder().id(3L).content("parent3").orders(1L).combinationId(combination.getId()).build());

        Pageable pageRequest = PageRequest.of(0, 3);
        Page<CombinationReplyDto> replyPageResult = new PageImpl<>(replyList);

        // when
        ResultActions perform = mockMvc.perform(get("/api/combination-reply")
                .param("page", "0")
                .param("size", "3"));

        // then
        then(replyService).should(times(1))
                .getCombinationReplyByCombination(anyLong(), any(Pageable.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.[0].id", is(1)))
                .andExpect(jsonPath("$.response.[1].id", is(2)))
                .andExpect(jsonPath("$.response.[1].id", is(3)));
    }
}