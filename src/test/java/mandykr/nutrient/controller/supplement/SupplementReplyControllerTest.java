package mandykr.nutrient.controller.supplement;

import mandykr.nutrient.dto.supplement.reply.SupplementReplyRequestDto;
import mandykr.nutrient.dto.supplement.reply.SupplementReplyResponseDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.supplement.SupplementReplyService;
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
import org.springframework.test.web.servlet.ResultMatcher;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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

    Supplement supplement;
    Member member;
    SupplementReply parent1;
    SupplementReply child1_1;
    SupplementReply child1_2;
    SupplementReply parent2;
    SupplementReply child2_1;


    @BeforeEach
    public void setup(){
        Supplement supplement = new Supplement();
        supplement.setId(1L);
        supplement.setName("test1");
        supplement.setRanking(4.2);
        this.supplement = supplement;

        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testMember");
        member.setName("KIM");
        this.member = member;

        parent1 = makeParent(1L, "TEST1", 1L, 1L);
        child1_1 = makeChild(2L, "TEST1-1", 1L, 2L, parent1);
        child1_2 = makeChild(3L, "TEST1-2", 1L, 2L, parent1);
        parent2 = makeParent(4L, "TEST2", 2L, 1L);
        child2_1 = makeChild(5L, "TEST2-1", 2L, 2L, parent2);

    }

    private SupplementReply makeParent(Long id, String content, Long groupId, Long groupOrder) {
        return SupplementReply.builder()
                .id(id)
                .supplement(supplement)
                .member(member)
                .parent(null)
                .content(content)
                .groups(groupId)
                .groupOrder(groupOrder)
                .deleteFlag(false)
                .build();
    }

    private SupplementReply makeChild(Long id, String content, Long groupId, Long groupOrder, SupplementReply parent) {
        return SupplementReply.builder()
                .id(id)
                .supplement(supplement)
                .member(member)
                .parent(parent)
                .content(content)
                .groups(groupId)
                .groupOrder(groupOrder)
                .deleteFlag(false)
                .build();
    }
    private void handlerCheck(
            ResultActions result,
            ResultMatcher status,
            Class<SupplementReplyController> className,
            String methodName) throws Exception {
        result.andDo(print())
                .andExpect(status)
                .andExpect(handler().handlerType(className))
                .andExpect(handler().methodName(methodName));
    }

    private void successCheckMany(ResultActions result, List<SupplementReplyResponseDto> supplementReplyResponseDtos) throws Exception {
        result.andDo(print())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()", is(supplementReplyResponseDtos.size())));
        for(int i=0; i < supplementReplyResponseDtos.size();++i){
            result.andDo(print())
                    .andExpect(jsonPath("$.response[" + i + "].id", is(supplementReplyResponseDtos.get(i).getId().intValue())))
                    .andExpect(jsonPath("$.response[" + i + "].content", is(supplementReplyResponseDtos.get(i).getContent())))
                    .andExpect(jsonPath("$.response[" + i + "].groups", is(supplementReplyResponseDtos.get(i).getGroups().intValue())))
                    .andExpect(jsonPath("$.response[" + i + "].groupOrder", is(supplementReplyResponseDtos.get(i).getGroupOrder().intValue())))
                    .andExpect(jsonPath("$.response[" + i + "].deleteFlag", is(supplementReplyResponseDtos.get(i).getDeleteFlag())));
        }
    }


    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기")
    public void 영양제_부모_댓글_보기() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        supplementReplyResponseDtos.add(new SupplementReplyResponseDto(parent1));
        supplementReplyResponseDtos.add(new SupplementReplyResponseDto(parent2));
        given(supplementReplyService.getSupplementRepliesBySupplement(1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1")
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "getSupplementRepliesBySupplement");
        successCheckMany(result, supplementReplyResponseDtos);
    }





    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기(데이터 없음)")
    public void 영양제_부모_댓글_보기_데이터없음() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        given(supplementReplyService.getSupplementRepliesBySupplement(1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        handlerCheck(result, status().isOk(), SupplementReplyController.class, "getSupplementRepliesBySupplement");
        successCheckMany(result, supplementReplyResponseDtos);
    }
   /*

    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기(영양제 없음)")
    public void 영양제_부모_댓글_보기_영양제없음() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        given(supplementReplyService.getSupplementRepliesBySupplement(1L)).willThrow(new EntityNotFoundException("not found Supplement : 1"));

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementRepliesBySupplement"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("not found Supplement : 1")));
    }

    @Test
    @DisplayName("부모댓글의 대댓글 보기")
    public void 부모댓글의_대댓글_보기() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        supplementReplyResponseDtos.add(
            new SupplementReplyResponseDto(
                SupplementReply.builder()
                .id(2L)
                .supplement(saveSupplement)
                .member(saveMember)
                .parent(SupplementReply.builder().id(1L).build())
                .content("reply1-1")
                .groups(1L)
                .groupOrder(2L)
                .deleteFlag(false)
                .build()
            ));
        supplementReplyResponseDtos.add(
            new SupplementReplyResponseDto(
                SupplementReply.builder()
                .id(3L)
                .supplement(saveSupplement)
                .member(saveMember)
                .parent(SupplementReply.builder().id(1L).build())
                .content("reply1-2")
                .groups(1L)
                .groupOrder(3L)
                .deleteFlag(false)
                .build()
            ));
        given(supplementReplyService.getSupplementRepliesByParent(1L, 1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementRepliesByParent"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()", is(2)))
                .andExpect(jsonPath("$.response[0].id", is(2)))
                .andExpect(jsonPath("$.response[0].content", is("reply1-1")))
                .andExpect(jsonPath("$.response[0].groups", is(1)))
                .andExpect(jsonPath("$.response[0].groupOrder", is(2)))
                .andExpect(jsonPath("$.response[0].deleteFlag", is(false)))
                .andExpect(jsonPath("$.response[1].id", is(3)))
                .andExpect(jsonPath("$.response[1].content", is("reply1-2")))
                .andExpect(jsonPath("$.response[1].groups", is(1)))
                .andExpect(jsonPath("$.response[1].groupOrder", is(3)))
                .andExpect(jsonPath("$.response[1].deleteFlag", is(false)));
    }


    @Test
    @DisplayName("부모댓글의 대댓글 보기(데이터없음)")
    public void 부모댓글의_대댓글_보기_데이터없음() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        given(supplementReplyService.getSupplementRepliesByParent(1L, 1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementRepliesByParent"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()", is(0)));
    }

    @Test
    @DisplayName("부모댓글의 대댓글 보기(영양제없음)")
    public void 부모댓글의_대댓글_보기_영양제없음() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        given(supplementReplyService.getSupplementRepliesByParent(1L, 1L)).willThrow(new EntityNotFoundException("not found Supplement : 1"));

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementRepliesByParent"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("not found Supplement : 1")));
    }

    @Test
    @DisplayName("댓글 달기 테스트")
    public void 댓글_달기_테스트() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                    SupplementReply.builder()
                        .id(1L)
                        .supplement(saveSupplement)
                        .member(saveMember)
                        .parent(null)
                        .content("this is review content!")
                        .groups(1L)
                        .groupOrder(1L)
                        .deleteFlag(false)
                        .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.createSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willReturn(supplementReplyResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                post("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"this is review content!\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("createSupplementReply"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(1)))
                .andExpect(jsonPath("$.response.content", is("this is review content!")))
                .andExpect(jsonPath("$.response.groups", is(1)))
                .andExpect(jsonPath("$.response.groupOrder", is(1)))
                .andExpect(jsonPath("$.response.deleteFlag", is(false)));
    }

    @Test
    @DisplayName("댓글 달기 오류(비어있을때)")
    public void 댓글_달기_댓글_비어있을때() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(1L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(null)
                                .content("")
                                .groups(1L)
                                .groupOrder(1L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.createSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willReturn(supplementReplyResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                post("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("createSupplementReply"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("must not be empty")));
    }
    
    @Test
    @DisplayName("댓글 달기 오류(50자 초과일때)")
    public void 댓글_달기_댓글_50자_초과일때() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(1L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(null)
                                .content("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                                .groups(1L)
                                .groupOrder(1L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.createSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willReturn(supplementReplyResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                post("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("createSupplementReply"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("size must be between 1 and 50")));
    }



    @Test
    @DisplayName("댓글 달기 테스트(영양제 없음)")
    public void 댓글_달기_테스트_영양제없음() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(1L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(null)
                                .content("this is review content!")
                                .groups(1L)
                                .groupOrder(1L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.createSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willThrow(new EntityNotFoundException("not found Supplement : 1"));

        //when
        ResultActions result = mockMvc.perform(
                post("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"this is review content!\"}")
        );
        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("createSupplementReply"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("not found Supplement : 1")));
    }

    @Test
    @DisplayName("대댓글 달기 테스트")
    public void 대댓글_달기_테스트() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(2L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(SupplementReply.builder().id(1L).groups(1L).groupOrder(1L).deleteFlag(false).build())
                                .content("this is review content!")
                                .groups(1L)
                                .groupOrder(2L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.createSupplementReply(anyLong(), anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willReturn(supplementReplyResponseDto);

        //when
        ResultActions result = mockMvc.perform(
                post("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"this is review content!\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("createSupplementReplyByReply"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(2)))
                .andExpect(jsonPath("$.response.content", is("this is review content!")))
                .andExpect(jsonPath("$.response.groups", is(1)))
                .andExpect(jsonPath("$.response.groupOrder", is(2)))
                .andExpect(jsonPath("$.response.deleteFlag", is(false)));
    }

    @Test
    @DisplayName("대댓글 달기 테스트(영양제없음)")
    public void 대댓글_달기_테스트_영양제없음() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(2L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(SupplementReply.builder().id(1L).groups(1L).groupOrder(1L).deleteFlag(false).build())
                                .content("this is review content!")
                                .groups(1L)
                                .groupOrder(2L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.createSupplementReply(anyLong(), anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willThrow(new EntityNotFoundException("not found Supplement : 1"));

        //when
        ResultActions result = mockMvc.perform(
                post("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"this is review content!\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("createSupplementReplyByReply"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("not found Supplement : 1")));
    }

    @Test
    @DisplayName("대댓글 달기 테스트(부모 댓글 없음)")
    public void 대댓글_달기_테스트_부모_댓글_없음() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(2L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(SupplementReply.builder().id(1L).groups(1L).groupOrder(1L).deleteFlag(false).build())
                                .content("this is review content!")
                                .groups(1L)
                                .groupOrder(2L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.createSupplementReply(anyLong(), anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willThrow(new EntityNotFoundException("not found SupplementReply : 1"));

        //when
        ResultActions result = mockMvc.perform(
                post("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"this is review content!\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("createSupplementReplyByReply"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("not found SupplementReply : 1")));
    }


    @Test
    @DisplayName("댓글 수정 테스트")
    public void 댓글_수정_테스트() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(1L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(null)
                                .content("update review content!")
                                .groups(1L)
                                .groupOrder(1L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.updateSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willReturn(supplementReplyResponseDto);
        //when
        ResultActions result = mockMvc.perform(
                put("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"update review content!\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("updateSupplementReply"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(1)))
                .andExpect(jsonPath("$.response.content", is("update review content!")))
                .andExpect(jsonPath("$.response.groups", is(1)))
                .andExpect(jsonPath("$.response.groupOrder", is(1)))
                .andExpect(jsonPath("$.response.deleteFlag", is(false)));
    }


    @Test
    @DisplayName("댓글 수정 테스트(댓글Id, Member 에 해당되는 데이터 없음)")
    public void 댓글_수정_테스트_오류() throws Exception {
        //given
        SupplementReplyResponseDto supplementReplyResponseDto =
                new SupplementReplyResponseDto(
                        SupplementReply.builder()
                                .id(1L)
                                .supplement(saveSupplement)
                                .member(saveMember)
                                .parent(null)
                                .content("update review content!")
                                .groups(1L)
                                .groupOrder(1L)
                                .deleteFlag(false)
                                .build()
                );
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        given(supplementReplyService.updateSupplementReply(anyLong(), any(Member.class), any(SupplementReplyRequestDto.class))).willThrow(new EntityNotFoundException("not found SupplementReply : 1"));
        //when
        ResultActions result = mockMvc.perform(
                put("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"update review content!\"}")
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("updateSupplementReply"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("not found SupplementReply : 1")));
    }


    @Test
    @DisplayName("댓글 삭제 테스트")
    public void 댓글_삭제_테스트() throws Exception {
        //given
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));


        //when
        ResultActions result = mockMvc.perform(
                delete("/supplement-reply/1")
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("deleteSupplementReply"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response", is(true)));
    }

    @Test
    @DisplayName("댓글 삭제 테스트(오류)")
    public void 댓글_삭제_테스트_오류() throws Exception {
        //given
        given(memberRepository.findById("testMemberId1")).willReturn(Optional.ofNullable(saveMember));
        doThrow(new EntityNotFoundException("not found SupplementReply : 1"))
                .when(supplementReplyService).deleteSupplementReply(anyLong(), any(Member.class));

        //when
        ResultActions result = mockMvc.perform(
                delete("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("deleteSupplementReply"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status", is(400)))
                .andExpect(jsonPath("$.error.message", is("not found SupplementReply : 1")));
    }


     */
}