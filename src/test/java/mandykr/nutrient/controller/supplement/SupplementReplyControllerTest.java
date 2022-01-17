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

    Supplement saveSupplement;
    Member saveMember;

    @BeforeEach
    public void setup(){
        Supplement supplement = new Supplement();
        supplement.setId(1L);
        supplement.setName("test1");
        supplement.setRanking(4.2);
        this.saveSupplement = supplement;

        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testMember");
        member.setName("KIM");
        this.saveMember = member;

    }

    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기")
    public void 영양제_부모_댓글_보기() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        supplementReplyResponseDtos.add(
            new SupplementReplyResponseDto(
                SupplementReply
                    .builder()
                    .id(1L)
                    .supplement(saveSupplement)
                    .member(saveMember)
                    .parent(null)
                    .content("reply1")
                    .groups(1L)
                    .groupOrder(1L)
                    .deleteFlag(false)
                    .build()
            )
        );
        supplementReplyResponseDtos.add(
            new SupplementReplyResponseDto(
                SupplementReply.builder()
                        .id(2L)
                        .supplement(saveSupplement)
                        .member(saveMember)
                        .parent(null)
                        .content("reply2")
                        .groups(2L)
                        .groupOrder(1L)
                        .deleteFlag(false)
                        .build()
            )
        );
        given(supplementReplyService.getSupplementReplyBySupplement(1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1")
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementReplyBySupplement"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()", is(2)))
                .andExpect(jsonPath("$.response[0].id", is(1)))
                .andExpect(jsonPath("$.response[0].content", is("reply1")))
                .andExpect(jsonPath("$.response[0].groups", is(1)))
                .andExpect(jsonPath("$.response[0].groupOrder", is(1)))
                .andExpect(jsonPath("$.response[0].deleteFlag", is(false)))
                .andExpect(jsonPath("$.response[1].id", is(2)))
                .andExpect(jsonPath("$.response[1].content", is("reply2")))
                .andExpect(jsonPath("$.response[1].groups", is(2)))
                .andExpect(jsonPath("$.response[1].groupOrder", is(1)))
                .andExpect(jsonPath("$.response[1].deleteFlag", is(false)));
    }


    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기(데이터 없음)")
    public void 영양제_부모_댓글_보기_데이터없음() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        given(supplementReplyService.getSupplementReplyBySupplement(1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementReplyBySupplement"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()", is(0)));
    }


    @Test
    @DisplayName("해당 영양제의 부모 댓글 보기(영양제 없음)")
    public void 영양제_부모_댓글_보기_영양제없음() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        given(supplementReplyService.getSupplementReplyBySupplement(1L)).willThrow(new EntityNotFoundException("not found Supplement : 1"));

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementReplyBySupplement"))
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
        given(supplementReplyService.getSupplementReplyBySupplementWithParent(1L, 1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementReplyList"))
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
        given(supplementReplyService.getSupplementReplyBySupplementWithParent(1L, 1L)).willReturn(supplementReplyResponseDtos);

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementReplyList"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()", is(0)));
    }

    @Test
    @DisplayName("부모댓글의 대댓글 보기(영양제없음)")
    public void 부모댓글의_대댓글_보기_영양제없음() throws Exception {
        //given
        List<SupplementReplyResponseDto> supplementReplyResponseDtos = new ArrayList<>();
        given(supplementReplyService.getSupplementReplyBySupplementWithParent(1L, 1L)).willThrow(new EntityNotFoundException("not found Supplement : 1"));

        //when
        ResultActions result = mockMvc.perform(
                get("/supplement-reply/1/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementReplyController.class))
                .andExpect(handler().methodName("getSupplementReplyList"))
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
    
}