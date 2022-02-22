package mandykr.nutrient.controller.supplement;

import mandykr.nutrient.dto.supplement.reply.SupplementReplyDto;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import mandykr.nutrient.repository.member.MemberRepository;
import mandykr.nutrient.service.supplement.SupplementReplyService;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplementReplyController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SupplementReplyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SupplementReplyService supplementReplyService;

    @MockBean
    MemberRepository memberRepository;


    final static int PAGE = 1;
    final static int PAGE_SIZE = 2;
    Supplement supplement;
    Member member;
    SupplementReplyDto parent1;
    SupplementReplyDto child1_1;
    SupplementReplyDto child1_2;
    SupplementReplyDto parent2;
    SupplementReplyDto child2_1;
    List<SupplementReplyDto> parentList = new ArrayList<>();
    List<SupplementReplyDto> childList = new ArrayList<>();

    Pageable pageRequest;
    Page<SupplementReplyDto> parentReplyPageResult;
    Page<SupplementReplyDto> childReplyPageResult;

    @BeforeEach
    public void setup(){
        this.supplement = Supplement.builder()
                        .id(1L)
                        .name("test1")
                        .ranking(4.2)
                        .build();

        Member member = new Member();
        //member.setId(1L);
        //member.setMemberId("testMember");
        //member.setName("KIM");
        this.member = member;

        parent1 = makeParent(1L, "TEST1", 1L, 1L);
        child1_1 = makeChild(2L, "TEST1-1", 1L, 2L, parent1.getId());
        child1_2 = makeChild(3L, "TEST1-2", 1L, 2L, parent1.getId());
        parent2 = makeParent(4L, "TEST2", 2L, 1L);
        child2_1 = makeChild(5L, "TEST2-1", 2L, 2L, parent2.getId());
        parentList.add(parent1);
        childList.add(child1_1);
        childList.add(child1_2);
        parentList.add(parent2);

        pageRequest = new PageRequestUtil(PAGE, PAGE_SIZE).getPageable();
        parentReplyPageResult = new PageImpl<>(parentList, pageRequest, parentList.size());
        childReplyPageResult = new PageImpl<>(childList, pageRequest, childList.size());
    }

    private SupplementReplyDto makeParent(Long id, String content, Long groupId, Long groupOrder) {
        return new SupplementReplyDto(
                SupplementReply.builder()
                        .id(id)
                        .supplement(supplement)
                        .member(member)
                        .content(content)
                        .groups(groupId)
                        .groupOrder(groupOrder)
                        .deleted(false)
                        .build()
        );
    }

    private SupplementReplyDto makeChild(Long id, String content, Long groupId, Long groupOrder, Long parent) {
        return new SupplementReplyDto(
                SupplementReply.builder()
                .id(id)
                .supplement(supplement)
                .member(member)
                .content(content)
                .parent(SupplementReply.builder().id(parent).build())
                .groups(groupId)
                .groupOrder(groupOrder)
                .deleted(false)
                .build()
        );
    }
    private void handlerCheck(
            ResultActions result,
            ResultMatcher status,
            Class<SupplementReplyController> className,
            String methodName) throws Exception {
        result
                .andExpect(status)
                .andExpect(handler().handlerType(className))
                .andExpect(handler().methodName(methodName));
    }

    private void successCheckMany(ResultActions result, Page<SupplementReplyDto> replyPageResult) throws Exception {
        result
                .andExpect(jsonPath("$.response.size", is(replyPageResult.getContent().size())))
                .andExpect(jsonPath("$.response.number", is(replyPageResult.getNumber())));
        List<SupplementReplyDto> content = replyPageResult.getContent();

        for(int i=0; i<content.size();++i){
            Integer parent = content.get(i).getParent() == null ?
                    null : content.get(i).getParent().intValue();
            result
                    .andExpect(jsonPath("$.response.content[" + i + "].id", is(content.get(i).getId().intValue())))
                    .andExpect(jsonPath("$.response.content[" + i + "].content", is(content.get(i).getContent())))
                    .andExpect(jsonPath("$.response.content[" + i + "].groups", is(content.get(i).getGroups().intValue())))
                    .andExpect(jsonPath("$.response.content[" + i + "].groupOrder", is(content.get(i).getGroupOrder().intValue())))
                    .andExpect(jsonPath("$.response.content[" + i + "].deleted", is(content.get(i).getDeleted())))
                    .andExpect(jsonPath("$.response.content[" + i + "].parent", is(parent)))
                    .andExpect(jsonPath("$.response.content[" + i + "].supplement", is(content.get(i).getSupplement().intValue())));
        }
    }

    private void successCheckOne(ResultActions result, SupplementReplyDto supplementReplyDto) throws Exception {
        Integer parent = supplementReplyDto.getParent() == null ?
                null : supplementReplyDto.getParent().intValue();
        result
                .andExpect(jsonPath("$.response.id", is(supplementReplyDto.getId().intValue())))
                .andExpect(jsonPath("$.response.content", is(supplementReplyDto.getContent())))
                .andExpect(jsonPath("$.response.groups", is(supplementReplyDto.getGroups().intValue())))
                .andExpect(jsonPath("$.response.groupOrder", is(supplementReplyDto.getGroupOrder().intValue())))
                .andExpect(jsonPath("$.response.deleted", is(supplementReplyDto.getDeleted())))
                .andExpect(jsonPath("$.response.parent", is(parent)))
                .andExpect(jsonPath("$.response.supplement", is(supplementReplyDto.getSupplement().intValue())));
    }

    private void successCheckEmpty(ResultActions result, Page<SupplementReplyDto> emptyPages) throws Exception {
        result
                .andExpect(jsonPath("$.response.empty", is(true)))
                .andExpect(jsonPath("$.response.last", is(true)))
                .andExpect(jsonPath("$.response.totalPages", is(0)));;
    }

    private void failCheck(ResultActions result, int errorCode, String errorMessage) throws Exception {
        result
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(errorCode)))
                .andExpect(jsonPath("$.error.message", is(errorMessage)));
    }

    private MultiValueMap<String, String> toMultiValueMap(Pageable pageRequest) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("page", String.valueOf(pageRequest.getPageNumber()));
        map.add("size", String.valueOf(pageRequest.getPageSize()));
        return map;
    }

    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기")
    public void 영양제_부모_댓글_보기() throws Exception {
        //given

        //when
        when(supplementReplyService.getSupplementRepliesWithParent(anyLong(), any(PageRequest.class)))
                .thenReturn(parentReplyPageResult);

        ResultActions result = mockMvc.perform(
                get("/api/supplement-reply/{supplementId}","1")
                .params(toMultiValueMap(pageRequest))
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "getSupplementRepliesWithParent");
        successCheckMany(result, parentReplyPageResult);
    }




    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기(데이터 없음)")
    public void 영양제_부모_댓글_보기_데이터없음() throws Exception {
        //given
        Page<SupplementReplyDto> emptyPages = new PageImpl<>(new ArrayList<>(), pageRequest, 0);

        //when
        when(supplementReplyService.getSupplementRepliesWithParent(anyLong(), any(PageRequest.class)))
                .thenReturn(emptyPages);

        ResultActions result = mockMvc.perform(
                get("/api/supplement-reply/{supplementId}","1")
                .params(toMultiValueMap(pageRequest))
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "getSupplementRepliesWithParent");
        successCheckEmpty(result, emptyPages);
    }

    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기(영양제 없음)")
    public void 영양제_부모_댓글_보기_영양제없음() throws Exception {
        //given

        //when
        when(supplementReplyService.getSupplementRepliesWithParent(anyLong(), any(PageRequest.class)))
                .thenThrow(new EntityNotFoundException("not found Supplement : 1"));
        ResultActions result = mockMvc.perform(
                get("/api/supplement-reply/{supplementId}","1")
                .params(toMultiValueMap(pageRequest))
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "getSupplementRepliesWithParent");
        failCheck(result, 400, "not found Supplement : 1");
    }



    @Test
    @DisplayName("부모댓글의 대댓글 보기")
    public void 부모댓글의_대댓글_보기() throws Exception {
        //given

        //when
        when(supplementReplyService.getSupplementRepliesWithChild(anyLong(), anyLong(), any(PageRequest.class)))
                .thenReturn(childReplyPageResult);
        ResultActions result = mockMvc.perform(
                get("/api/supplement-reply/{supplementId}/{parentId}", "1", "1")
                .params(toMultiValueMap(pageRequest))
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "getSupplementRepliesWithChild");
        successCheckMany(result, childReplyPageResult);
    }


    @Test
    @DisplayName("부모댓글의 대댓글 보기(데이터없음)")
    public void 부모댓글의_대댓글_보기_데이터없음() throws Exception {
        //given
        Page<SupplementReplyDto> emptyPages = new PageImpl<>(new ArrayList<>(), pageRequest, 0);

        //when
        when(supplementReplyService.getSupplementRepliesWithChild(anyLong(), anyLong(), any(PageRequest.class)))
                .thenReturn(emptyPages);
        ResultActions result = mockMvc.perform(
                get("/api/supplement-reply/{supplementId}/{parentId}", "1", "1")
                .params(toMultiValueMap(pageRequest))
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "getSupplementRepliesWithChild");
        successCheckEmpty(result, emptyPages);
    }




    @Test
    @DisplayName("부모댓글의 대댓글 보기(영양제없음)")
    public void 부모댓글의_대댓글_보기_영양제없음() throws Exception {
        //given

        //when
        when(supplementReplyService.getSupplementRepliesWithChild(anyLong(), anyLong(), any(PageRequest.class)))
                .thenThrow(new EntityNotFoundException("not found Supplement : 1"));
        ResultActions result = mockMvc.perform(
                get("/api/supplement-reply/{supplementId}/{parentId}", "1", "1")
                .params(toMultiValueMap(pageRequest))
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "getSupplementRepliesWithChild");
        failCheck(result, 400, "not found Supplement : 1");
    }


    @Test
    @DisplayName("댓글 달기 테스트")
    public void 댓글_달기_테스트() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(supplementReplyService.createSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequest.class)))
                .thenReturn(parent1);

        ResultActions result = mockMvc.perform(
                post("/api/supplement-reply/{supplementId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"TEST1\"}")
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "createSupplementReply");
        successCheckOne(result, parent1);
    }

    @Test
    @DisplayName("댓글 달기 오류(비어있을때)")
    public void 댓글_달기_댓글_비어있을때() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        ResultActions result = mockMvc.perform(
                post("/api/supplement-reply/{supplementId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\"}")
        );

        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "createSupplementReply");
        failCheck(result, 400, "must not be empty");
    }

    @Test
    @DisplayName("댓글 달기 오류(50자 초과일때)")
    public void 댓글_달기_댓글_50자_초과일때() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(
                post("/api/supplement-reply/{supplementId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}")
        );

        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "createSupplementReply");
        failCheck(result, 400, "size must be between 1 and 50");
    }

    @Test
    @DisplayName("댓글 달기 테스트(영양제 없음)")
    public void 댓글_달기_테스트_영양제없음() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(supplementReplyService.createSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequest.class)))
                .thenThrow(new EntityNotFoundException("not found Supplement : " + parent1.getId()));
        ResultActions result = mockMvc.perform(
                post("/api/supplement-reply/{supplementId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"TEST1\"}")
        );
        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "createSupplementReply");
        failCheck(result, 400, "not found Supplement : " + parent1.getId());
    }

    @Test
    @DisplayName("대댓글 달기 테스트")
    public void 대댓글_달기_테스트() throws Exception {
        //given


        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(supplementReplyService.createSupplementReply(anyLong(), anyLong(), any(Member.class), any(SupplementReplyRequest.class)))
                .thenReturn(child1_1);
        ResultActions result = mockMvc.perform(
                post("/api/supplement-reply/{supplementId}/{replyId}", "1", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"TEST1-1\"}")
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "createSupplementReplyByReply");
        successCheckOne(result, child1_1);
    }

    @Test
    @DisplayName("대댓글 달기 테스트(영양제없음)")
    public void 대댓글_달기_테스트_영양제없음() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(supplementReplyService.createSupplementReply(anyLong(), anyLong(), any(Member.class), any(SupplementReplyRequest.class)))
                .thenThrow(new EntityNotFoundException("not found Supplement : " + child1_1.getId()));
        ResultActions result = mockMvc.perform(
                post("/api/supplement-reply/{supplementId}/{replyId}", "1", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"TEST1-1\"}")
        );
        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "createSupplementReplyByReply");
        failCheck(result, 400, "not found Supplement : "+ child1_1.getId());
    }


    @Test
    @DisplayName("대댓글 달기 테스트(부모 댓글 없음)")
    public void 대댓글_달기_테스트_부모_댓글_없음() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(supplementReplyService.createSupplementReply(anyLong(), anyLong(), any(Member.class), any(SupplementReplyRequest.class)))
                .thenThrow(new EntityNotFoundException("not found SupplementReply : 1"));

        ResultActions result = mockMvc.perform(
                post("/api/supplement-reply/{supplementId}/{replyId}", "1", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"this is review content!\"}")
        );

        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "createSupplementReplyByReply");
        failCheck(result, 400, "not found SupplementReply : 1");
    }


    @Test
    @DisplayName("댓글 수정 테스트")
    public void 댓글_수정_테스트() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(supplementReplyService.updateSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequest.class)))
                .thenReturn(parent1);

        ResultActions result = mockMvc.perform(
                put("/api/supplement-reply/{replyId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"TEST2\"}")
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "updateSupplementReply");
        successCheckOne(result, parent1);
    }


    @Test
    @DisplayName("댓글 수정 테스트(댓글Id, Member 에 해당되는 데이터 없음)")
    public void 댓글_수정_테스트_오류() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(supplementReplyService.updateSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequest.class)))
                .thenThrow(new EntityNotFoundException("not found SupplementReply : 1"));
        ResultActions result = mockMvc.perform(
                put("/api/supplement-reply/{replyId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"TEST1\"}")
        );

        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "updateSupplementReply");
        failCheck(result, 400, "not found SupplementReply : 1");
    }


    @Test
    @DisplayName("댓글 삭제 테스트")
    public void 댓글_삭제_테스트() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        ResultActions result = mockMvc.perform(
                delete("/api/supplement-reply/{replyId}", "1")
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "deleteSupplementReply");
        result.andExpect(jsonPath("$.response", is(true)));
    }

    @Test
    @DisplayName("댓글 삭제 테스트(오류)")
    public void 댓글_삭제_테스트_오류() throws Exception {
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        doThrow(new EntityNotFoundException("not found SupplementReply : 1"))
                .when(supplementReplyService).deleteSupplementReply(anyLong(), any(Member.class));
        ResultActions result = mockMvc.perform(
                delete("/api/supplement-reply/{replyId}", "1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().is4xxClientError(), SupplementReplyController.class, "deleteSupplementReply");
        failCheck(result, 400, "not found SupplementReply : 1");
    }

}