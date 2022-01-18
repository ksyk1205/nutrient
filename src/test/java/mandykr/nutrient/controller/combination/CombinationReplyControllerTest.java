package mandykr.nutrient.controller.combination;

import com.fasterxml.jackson.databind.ObjectMapper;
import mandykr.nutrient.dto.combination.reply.CombinationReplyCreateFormDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyUpdateFormDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.service.combination.CombinationReplyService;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CombinationReplyController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CombinationReplyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CombinationReplyService replyService;

    Combination combination;
    ArrayList<CombinationReplyDto> parentReplyList;
    CombinationReplyDto parent;
    ArrayList<CombinationReplyDto> childReplyList;

    Pageable pageRequest;
    int page = 1;
    int size = 2;

    Page<CombinationReplyDto> parentReplyPageResult;
    Page<CombinationReplyDto> childReplyPageResult;

    @BeforeEach
    void setup() {
        combination = new Combination(1L);

        parentReplyList = new ArrayList<>();
        parent = CombinationReplyDto.builder().id(1L).content("parent1").orders(1L).combinationId(combination.getId()).build();
        parentReplyList.add(parent);
        parentReplyList.add(CombinationReplyDto.builder().id(2L).content("parent2").orders(1L).combinationId(combination.getId()).build());
        parentReplyList.add(CombinationReplyDto.builder().id(3L).content("parent3").orders(1L).combinationId(combination.getId()).build());

        childReplyList = new ArrayList<>();
        childReplyList.add(CombinationReplyDto.builder().id(11L).content("child1_1").orders(2L).parentId(parent.getId()).combinationId(combination.getId()).build());
        childReplyList.add(CombinationReplyDto.builder().id(12L).content("child1_2").orders(2L).parentId(parent.getId()).combinationId(combination.getId()).build());
        childReplyList.add(CombinationReplyDto.builder().id(13L).content("child1_3").orders(2L).parentId(parent.getId()).combinationId(combination.getId()).build());

        pageRequest = new PageRequestUtil(page, size).getPageable();
        parentReplyPageResult = new PageImpl<>(parentReplyList, pageRequest, parentReplyList.size());
        childReplyPageResult = new PageImpl<>(childReplyList, pageRequest, childReplyList.size());
    }

    @Test
    @DisplayName("부모 댓글을 페이징으로 조회하면 댓글 DTO 목록을 반환한다.")
    void getParentsReplyByCombination() throws Exception {
        // given
        given(replyService.getParentsReplyByCombination(anyLong(), any(PageRequest.class)))
                .willReturn(parentReplyPageResult);

        // when
        ResultActions perform = mockMvc.perform(get("/api/combination-reply/{combinationId}", "1")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        then(replyService).should(times(1))
                .getParentsReplyByCombination(anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.*", hasSize(parentReplyList.size())))
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(page - 1)))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.response.content.[0].id", is(1)))
                .andExpect(jsonPath("$.response.content.[1].id", is(2)))
                .andExpect(jsonPath("$.response.content.[2].id", is(3)));
    }

    @Test
    @DisplayName("자식 댓글을 페이징으로 조회하면 댓글 DTO 목록을 반환한다.")
    void getChildrenReplyByParent() throws Exception {
        // given
        given(replyService.getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class)))
                .willReturn(childReplyPageResult);

        // when
        ResultActions perform = mockMvc.perform(get("/api/combination-reply/{combinationId}/{parentId}", "1", "1")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        then(replyService).should(times(1))
                .getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.*", hasSize(childReplyList.size())))
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(page - 1)))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.response.content.[0].id", is(11)))
                .andExpect(jsonPath("$.response.content.[1].id", is(12)))
                .andExpect(jsonPath("$.response.content.[2].id", is(13)));
    }

    @Test
    @DisplayName("자식 댓글을 저장하면 자식 리스트를 반환한다.")
    void createChildReply() throws Exception {
        // given
        CombinationReplyCreateFormDto childReplyDto = CombinationReplyCreateFormDto.builder()
                .content("childReply")
                .orders(CombinationReply.CHILD_ORDERS)
                .combinationId(combination.getId())
                .parentId(1L)
                .build();
        CombinationReplyDto saveChildReplyDto = CombinationReplyDto.builder()
                .content("reply")
                .orders(CombinationReply.CHILD_ORDERS)
                .combinationId(combination.getId())
                .parentId(1L)
                .build();

        given(replyService.createReply(any(CombinationReplyCreateFormDto.class)))
                .willReturn(saveChildReplyDto);
        given(replyService.getParentOrChildReplyList(any(CombinationReplyDto.class), any(PageRequest.class)))
                .willReturn(childReplyPageResult);

        // when
        ResultActions perform = mockMvc.perform(post("/api/combination-reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(childReplyDto))
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

//        String message = perform.andReturn().getResolvedException().getMessage();
//        System.out.println("message = " + message);

        // then
        then(replyService).should(times(1))
                .createReply(any(CombinationReplyCreateFormDto.class));
        then(replyService).should(times(1))
                .getParentOrChildReplyList(any(CombinationReplyDto.class), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.*", hasSize(childReplyList.size())))
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(page - 1)))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.response.content.[0].id", is(11)))
                .andExpect(jsonPath("$.response.content.[1].id", is(12)))
                .andExpect(jsonPath("$.response.content.[2].id", is(13)));
    }

    @Test
    @DisplayName("부모 댓글을 수정하면 부모 리스트를 반환한다.")
    void updateParentReply() throws Exception {
        // given
        CombinationReplyUpdateFormDto parentReplyDto = CombinationReplyUpdateFormDto.builder()
                .id(1L)
                .content("reply")
                .build();
        CombinationReplyDto updateParentReplyDto = CombinationReplyDto.builder()
                .id(parentReplyDto.getId())
                .content(parentReplyDto.getContent())
                .orders(CombinationReply.PARENT_ORDERS)
                .build();

        given(replyService.updateReply(any(CombinationReplyUpdateFormDto.class)))
                .willReturn(updateParentReplyDto);
        given(replyService.getParentOrChildReplyList(any(CombinationReplyDto.class), any(PageRequest.class)))
                .willReturn(parentReplyPageResult);

        // when
        ResultActions perform = mockMvc.perform(put("/api/combination-reply/{replyId}", String.valueOf(parentReplyDto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(parentReplyDto))
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        then(replyService).should(times(1))
                .updateReply(any(CombinationReplyUpdateFormDto.class));
        then(replyService).should(times(1))
                .getParentOrChildReplyList(any(CombinationReplyDto.class), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.*", hasSize(parentReplyList.size())))
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(page - 1)))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.response.content.[0].id", is(1)))
                .andExpect(jsonPath("$.response.content.[1].id", is(2)))
                .andExpect(jsonPath("$.response.content.[2].id", is(3)));
    }

    @Test
    @DisplayName("부모 댓글을 삭제하면 부모 리스트를 반환한다.")
    void deleteReply() throws Exception {
        // given
        given(replyService.getReplyDto(parent.getId()))
                .willReturn(parent);
        given(replyService.getParentOrChildReplyList(any(CombinationReplyDto.class), any(PageRequest.class)))
                .willReturn(parentReplyPageResult);

        // when
        ResultActions perform = mockMvc.perform(delete("/api/combination-reply/{replyId}", String.valueOf(parent.getId()))
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        then(replyService).should(times(1))
                .getReplyDto(parent.getId());
        then(replyService).should(times(1))
                .getParentOrChildReplyList(any(CombinationReplyDto.class), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.*", hasSize(parentReplyList.size())))
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(page - 1)))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.response.content.[0].id", is(1)))
                .andExpect(jsonPath("$.response.content.[1].id", is(2)))
                .andExpect(jsonPath("$.response.content.[2].id", is(3)));
    }

}