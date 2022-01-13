package mandykr.nutrient.controller.combination;

import com.fasterxml.jackson.databind.ObjectMapper;
import mandykr.nutrient.dto.combination.reply.CombinationReplyCreateFormDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyUpdateFormDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.service.combination.CombinationReplyService;
import mandykr.nutrient.util.PageRequestUtil;
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

    @Test
    @DisplayName("부모 댓글을 페이징으로 조회하면 댓글 DTO 목록을 반환한다.")
    void getCombinationReplyByCombination() throws Exception {
        // given
        final int page = 0;
        final int size = 3;

        Combination combination = new Combination(1L);
        ArrayList<CombinationReplyDto> replyList = new ArrayList<>();
        replyList.add(CombinationReplyDto.builder().id(1L).content("parent1").orders(1L).combinationId(combination.getId()).build());
        replyList.add(CombinationReplyDto.builder().id(2L).content("parent2").orders(1L).combinationId(combination.getId()).build());
        replyList.add(CombinationReplyDto.builder().id(3L).content("parent3").orders(1L).combinationId(combination.getId()).build());

        Pageable pageRequest = PageRequest.of(page, size);
        Page<CombinationReplyDto> replyPageResult = new PageImpl<>(replyList, pageRequest, replyList.size());

        given(replyService.getParentReplyByCombination(anyLong(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        ResultActions perform = mockMvc.perform(get("/api/combination-reply/{combinationId}", "1")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        then(replyService).should(times(1))
                .getParentReplyByCombination(anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.*", hasSize(size)))
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(page)))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.response.content.[0].id", is(1)))
                .andExpect(jsonPath("$.response.content.[1].id", is(2)))
                .andExpect(jsonPath("$.response.content.[2].id", is(3)));
    }

    @Test
    @DisplayName("자식 댓글을 페이징으로 조회하면 댓글 DTO 목록을 반환한다.")
    void getCombinationReplyByParent() throws Exception {
        // given
        final int page = 0;
        final int size = 3;

        Combination combination = new Combination(1L);
        ArrayList<CombinationReplyDto> replyList = new ArrayList<>();
        CombinationReplyDto parent = CombinationReplyDto.builder().id(1L).content("parent").orders(1L).combinationId(combination.getId()).build();
        replyList.add(CombinationReplyDto.builder().id(2L).content("child1_1").orders(2L).parentId(parent.getId()).combinationId(combination.getId()).build());
        replyList.add(CombinationReplyDto.builder().id(3L).content("child1_2").orders(2L).parentId(parent.getId()).combinationId(combination.getId()).build());
        replyList.add(CombinationReplyDto.builder().id(4L).content("child1_3").orders(2L).parentId(parent.getId()).combinationId(combination.getId()).build());

        Pageable pageRequest = PageRequest.of(page, size);
        Page<CombinationReplyDto> replyPageResult = new PageImpl<>(replyList, pageRequest, replyList.size());

        given(replyService.getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        ResultActions perform = mockMvc.perform(get("/api/combination-reply/{combinationId}/{parentId}", "1", "1")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        then(replyService).should(times(1))
                .getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.*", hasSize(size)))
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(page)))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.response.content.[0].id", is(2)))
                .andExpect(jsonPath("$.response.content.[1].id", is(3)))
                .andExpect(jsonPath("$.response.content.[2].id", is(4)));
    }

    @Test
    @DisplayName("부모 댓글을 저장하면 부모 리스트를 반환한다.")
    void createParentReply() throws Exception {
        // given
        // 1. dto, saveReply
        Combination combination = new Combination(1L);
        CombinationReplyCreateFormDto parentDto = CombinationReplyCreateFormDto.builder()
                .content("parentReply")
                .orders(CombinationReply.PARENT_ORDERS)
                .combinationId(combination.getId())
                .build();
        CombinationReply saveParentReply = parentDto.createReply();

        // 2. replyList
        ArrayList<CombinationReplyDto> parentReplyList = new ArrayList<>();
        parentReplyList.add(CombinationReplyDto.builder().id(1L).content("parent1").orders(1L).combinationId(combination.getId()).build());
        parentReplyList.add(CombinationReplyDto.builder().id(2L).content("parent2").orders(1L).combinationId(combination.getId()).build());
        parentReplyList.add(CombinationReplyDto.builder().id(3L).content("parent3").orders(1L).combinationId(combination.getId()).build());

        // 3. page
        PageRequestUtil pageRequestUtil = new PageRequestUtil();
        Pageable pageRequest = pageRequestUtil.getPageable();
        Page<CombinationReplyDto> replyPageResult = new PageImpl<>(parentReplyList, pageRequest, parentReplyList.size());

        given(replyService.createReply(any(CombinationReplyCreateFormDto.class)))
                .willReturn(saveParentReply);
        given(replyService.getParentReplyByCombination(anyLong(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        ResultActions perform = mockMvc.perform(post("/api/combination-reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(parentDto))
                .param("page", String.valueOf(pageRequest.getPageNumber()))
                .param("size", String.valueOf(pageRequest.getPageSize())));

        // then
        then(replyService).should(times(1))
                .getParentReplyByCombination(anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(pageRequest.getPageNumber())))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(pageRequest.getPageSize())))
                .andExpect(jsonPath("$.response.content.[0].id", is(1)))
                .andExpect(jsonPath("$.response.content.[1].id", is(2)))
                .andExpect(jsonPath("$.response.content.[2].id", is(3)));
    }

    @Test
    @DisplayName("자식 댓글을 저장하면 자식 리스트를 반환한다.")
    void createChildReply() throws Exception {
        // given
        // 1. dto, saveReply
        Combination combination = new Combination(1L);
        CombinationReplyCreateFormDto childDto = CombinationReplyCreateFormDto.builder()
                .content("childReply")
                .orders(CombinationReply.CHILD_ORDERS)
                .combinationId(combination.getId())
                .parentId(1L)
                .build();
        CombinationReply saveParentReply = childDto.createReply();

        // 2. replyList
        ArrayList<CombinationReplyDto> childReplyList = new ArrayList<>();
        CombinationReplyDto parent1 = CombinationReplyDto.builder().id(1L).content("parent1").orders(1L).combinationId(combination.getId()).build();
        childReplyList.add(CombinationReplyDto.builder().id(2L).content("child1_1").orders(2L).parentId(parent1.getId()).combinationId(combination.getId()).build());
        childReplyList.add(CombinationReplyDto.builder().id(3L).content("child1_2").orders(2L).parentId(parent1.getId()).combinationId(combination.getId()).build());
        childReplyList.add(CombinationReplyDto.builder().id(4L).content("child1_3").orders(2L).parentId(parent1.getId()).combinationId(combination.getId()).build());

        // 3. page
        PageRequestUtil pageRequestUtil = new PageRequestUtil();
        Pageable pageRequest = pageRequestUtil.getPageable();
        Page<CombinationReplyDto> replyPageResult = new PageImpl<>(childReplyList, pageRequest, childReplyList.size());

        given(replyService.createReply(any(CombinationReplyCreateFormDto.class)))
                .willReturn(saveParentReply);
        given(replyService.getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        ResultActions perform = mockMvc.perform(post("/api/combination-reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(childDto))
                .param("page", String.valueOf(pageRequest.getPageNumber()))
                .param("size", String.valueOf(pageRequest.getPageSize())));

//        String message = perform.andReturn().getResolvedException().getMessage();
//        System.out.println("message = " + message);

        // then
        then(replyService).should(times(1))
                .getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(pageRequest.getPageNumber())))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(pageRequest.getPageSize())))
                .andExpect(jsonPath("$.response.content.[0].id", is(2)))
                .andExpect(jsonPath("$.response.content.[1].id", is(3)))
                .andExpect(jsonPath("$.response.content.[2].id", is(4)));
    }

    @Test
    @DisplayName("부모 댓글을 수정하면 부모 리스트를 반환한다.")
    void updateParentReply() throws Exception {
        // given
        // 1. dto, updateReply
        Combination combination = new Combination(1L);
        CombinationReplyUpdateFormDto parentDto = CombinationReplyUpdateFormDto.builder()
                .id(4L)
                .content("parentReply")
                .build();
        CombinationReply updateParentReply = CombinationReply.builder()
                .id(parentDto.getId())
                .content(parentDto.getContent())
                .orders(CombinationReply.PARENT_ORDERS)
                .combination(new Combination(1L))
                .build();

        // 2. replyList
        ArrayList<CombinationReplyDto> parentReplyList = new ArrayList<>();
        parentReplyList.add(CombinationReplyDto.builder().id(1L).content("parent1").orders(1L).combinationId(combination.getId()).build());
        parentReplyList.add(CombinationReplyDto.builder().id(2L).content("parent2").orders(1L).combinationId(combination.getId()).build());
        parentReplyList.add(CombinationReplyDto.builder().id(3L).content("parent3").orders(1L).combinationId(combination.getId()).build());
        parentReplyList.add(CombinationReplyDto.of(updateParentReply));

        // 3. page
        PageRequestUtil pageRequestUtil = new PageRequestUtil();
        Pageable pageRequest = pageRequestUtil.getPageable();
        Page<CombinationReplyDto> replyPageResult = new PageImpl<>(parentReplyList, pageRequest, parentReplyList.size());

        given(replyService.updateReply(any(CombinationReplyUpdateFormDto.class)))
                .willReturn(updateParentReply);
        given(replyService.getParentReplyByCombination(anyLong(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        ResultActions perform = mockMvc.perform(put("/api/combination-reply/{replyId}", String.valueOf(parentDto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(parentDto))
                .param("page", String.valueOf(pageRequest.getPageNumber()))
                .param("size", String.valueOf(pageRequest.getPageSize())));

        // then
        then(replyService).should(times(1))
                .getParentReplyByCombination(anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(pageRequest.getPageNumber())))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(pageRequest.getPageSize())))
                .andExpect(jsonPath("$.response.content.[0].id", is(1)))
                .andExpect(jsonPath("$.response.content.[1].id", is(2)))
                .andExpect(jsonPath("$.response.content.[2].id", is(3)))
                .andExpect(jsonPath("$.response.content.[3].id", is(4)));
    }

    @Test
    @DisplayName("자식 댓글을 수정하면 자식 리스트를 반환한다.")
    void updateChildReply() throws Exception {
        // given
        // 1. dto, updateReply
        Combination combination = new Combination(1L);
        CombinationReplyUpdateFormDto childDto = CombinationReplyUpdateFormDto.builder()
                .id(5L)
                .content("childReply")
                .build();
        CombinationReply updateChildReply = CombinationReply.builder()
                .id(childDto.getId())
                .content(childDto.getContent())
                .orders(CombinationReply.CHILD_ORDERS)
                .combination(new Combination(1L))
                .parent(new CombinationReply(1L))
                .build();

        // 2. replyList
        ArrayList<CombinationReplyDto> childReplyList = new ArrayList<>();
        CombinationReplyDto parent1 = CombinationReplyDto.builder().id(1L).content("parent1").orders(1L).combinationId(combination.getId()).build();
        childReplyList.add(CombinationReplyDto.builder().id(2L).content("child1_1").orders(2L).parentId(parent1.getId()).combinationId(combination.getId()).build());
        childReplyList.add(CombinationReplyDto.builder().id(3L).content("child1_2").orders(2L).parentId(parent1.getId()).combinationId(combination.getId()).build());
        childReplyList.add(CombinationReplyDto.builder().id(4L).content("child1_3").orders(2L).parentId(parent1.getId()).combinationId(combination.getId()).build());
        childReplyList.add(CombinationReplyDto.of(updateChildReply));

        // 3. page
        PageRequestUtil pageRequestUtil = new PageRequestUtil();
        Pageable pageRequest = pageRequestUtil.getPageable();
        Page<CombinationReplyDto> replyPageResult = new PageImpl<>(childReplyList, pageRequest, childReplyList.size());

        given(replyService.updateReply(any(CombinationReplyUpdateFormDto.class)))
                .willReturn(updateChildReply);
        given(replyService.getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        ResultActions perform = mockMvc.perform(put("/api/combination-reply/{replyId}", String.valueOf(childDto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(childDto))
                .param("page", String.valueOf(pageRequest.getPageNumber()))
                .param("size", String.valueOf(pageRequest.getPageSize())));

        // then
        then(replyService).should(times(1))
                .getChildrenReplyByParent(anyLong(), anyLong(), any(PageRequest.class));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.pageable.pageNumber", is(pageRequest.getPageNumber())))
                .andExpect(jsonPath("$.response.pageable.pageSize", is(pageRequest.getPageSize())))
                .andExpect(jsonPath("$.response.content.[0].id", is(2)))
                .andExpect(jsonPath("$.response.content.[1].id", is(3)))
                .andExpect(jsonPath("$.response.content.[2].id", is(4)))
                .andExpect(jsonPath("$.response.content.[3].id", is(5)));
    }

}