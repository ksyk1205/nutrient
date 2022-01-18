package mandykr.nutrient.service.supplement;

import mandykr.nutrient.dto.supplement.reply.SupplementReplyRequestDto;
import mandykr.nutrient.dto.supplement.reply.SupplementReplyResponseDto;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import mandykr.nutrient.repository.supplement.reply.SupplementReplyRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayName("SupplementReplyServiceTest")
class SupplementReplyServiceTest {

    SupplementReplyRepository supplementReplyRepository = mock(SupplementReplyRepository.class);

    SupplementRepository supplementRepository =  mock(SupplementRepository.class);

    SupplementReplyService supplementReplyService = new SupplementReplyService(supplementReplyRepository,supplementRepository);

    Supplement saveSupplement;

    Member saveMember;

    @BeforeEach
    void setup(){
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
    @DisplayName("영양제 부모 댓글 보기")
    public void 영양제_부모_댓글_보기(){
        //given
        List<SupplementReply> supplementReplies = new ArrayList<>();
        supplementReplies.add(
                SupplementReply.builder()
                        .id(1L)
                        .content("댓글1")
                        .supplement(saveSupplement)
                        .groups(1L)
                        .groupOrder(1L)
                        .deleteFlag(false)
                        .member(saveMember)
                        .build()
        );
        supplementReplies.add(
                SupplementReply.builder()
                        .id(2L)
                        .content("댓글2")
                        .supplement(saveSupplement)
                        .groups(2L)
                        .groupOrder(1L)
                        .deleteFlag(false)
                        .member(saveMember)
                        .build()
        );
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.of(saveSupplement));
        given(supplementReplyRepository.findBySupplementAndParentIsNull(any(Supplement.class),any())).willReturn(supplementReplies);

        //when
        List<SupplementReplyResponseDto> supplementRepliesBySupplement = supplementReplyService.getSupplementRepliesBySupplement(saveSupplement.getId());

        //then
        assertThat(supplementRepliesBySupplement.size()).isEqualTo(2);
        for(int i=0; i<supplementRepliesBySupplement.size(); ++i) {
            assertThat(supplementRepliesBySupplement.get(i).getId()).isEqualTo(supplementReplies.get(i).getId());
            assertThat(supplementRepliesBySupplement.get(i).getContent()).isEqualTo(supplementReplies.get(i).getContent());
            assertThat(supplementRepliesBySupplement.get(i).getDeleteFlag()).isEqualTo(supplementReplies.get(i).getDeleteFlag());
            assertThat(supplementRepliesBySupplement.get(i).getGroups()).isEqualTo(supplementReplies.get(i).getGroups());
            assertThat(supplementRepliesBySupplement.get(i).getGroupOrder()).isEqualTo(supplementReplies.get(i).getGroupOrder());
        }
    }

    @Test
    @DisplayName("영양제 부모 댓글 보기(영양제 없음)")
    public void 영양제_부모_댓글_영양제_없음(){
        //given
        given(supplementRepository.findById(saveSupplement.getId())).willThrow(new EntityNotFoundException("not found Supplement : " + saveSupplement.getId()));
        //when
        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.getSupplementRepliesBySupplement(saveSupplement.getId()),
                "not found Supplement : " + saveSupplement.getId()
                );
    }


    @Test
    @DisplayName("영양제 자식 댓글 보기")
    public void 영양제_자식_댓글_보기(){
        //given
        //부모
        SupplementReply parent = SupplementReply.builder()
                .id(1L)
                .content("댓글1")
                .supplement(saveSupplement)
                .groups(1L)
                .groupOrder(1L)
                .deleteFlag(false)
                .member(saveMember)
                .build();

        List<SupplementReply> supplementReplies = new ArrayList<>();
        supplementReplies.add(
                SupplementReply.builder()
                        .id(2L)
                        .content("댓글1-1")
                        .supplement(saveSupplement)
                        .groups(1L)
                        .groupOrder(2L)
                        .deleteFlag(false)
                        .member(saveMember)
                        .build()
        );
        supplementReplies.add(
                SupplementReply.builder()
                        .id(3L)
                        .content("댓글1-2")
                        .supplement(saveSupplement)
                        .groups(1L)
                        .groupOrder(3L)
                        .deleteFlag(false)
                        .member(saveMember)
                        .build()
        );
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.of(saveSupplement));
        given(supplementReplyRepository.findById(parent.getId())).willReturn(Optional.of(parent));
        given(supplementReplyRepository.findBySupplementAndParent(any(Supplement.class), any(SupplementReply.class), any())).willReturn(supplementReplies);

        //when
        List<SupplementReplyResponseDto> supplementRepliesBySupplement = supplementReplyService.getSupplementRepliesByParent(saveSupplement.getId(), parent.getId());

        //then
        assertThat(supplementRepliesBySupplement.size()).isEqualTo(2);
        for(int i=0; i<supplementRepliesBySupplement.size(); ++i) {
            assertThat(supplementRepliesBySupplement.get(i).getId()).isEqualTo(supplementReplies.get(i).getId());
            assertThat(supplementRepliesBySupplement.get(i).getContent()).isEqualTo(supplementReplies.get(i).getContent());
            assertThat(supplementRepliesBySupplement.get(i).getDeleteFlag()).isEqualTo(supplementReplies.get(i).getDeleteFlag());
            assertThat(supplementRepliesBySupplement.get(i).getGroups()).isEqualTo(supplementReplies.get(i).getGroups());
            assertThat(supplementRepliesBySupplement.get(i).getGroupOrder()).isEqualTo(supplementReplies.get(i).getGroupOrder());
        }
    }

    @Test
    @DisplayName("영양제 자식 댓글 보기(영양제 없음)")
    public void 영양제_자식_댓글_영양제_없음(){
        //given
        //부모
        SupplementReply parent = SupplementReply.builder()
                .id(1L)
                .content("댓글1")
                .supplement(saveSupplement)
                .groups(1L)
                .groupOrder(1L)
                .deleteFlag(false)
                .member(saveMember)
                .build();
        given(supplementRepository.findById(saveSupplement.getId())).willThrow(new EntityNotFoundException("not found Supplement : " + saveSupplement.getId()));
        //when
        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.getSupplementRepliesByParent(saveSupplement.getId(), parent.getId()),
                "not found Supplement : " + saveSupplement.getId()
        );
    }


    @Test
    @DisplayName("영양제 자식 댓글 보기(부모 댓글 없음)")
    public void 영양제_자식_댓글_부모_댓글_없음(){
        //given
        //부모
        SupplementReply parent = SupplementReply.builder()
                .id(1L)
                .content("댓글1")
                .supplement(saveSupplement)
                .groups(1L)
                .groupOrder(1L)
                .deleteFlag(false)
                .member(saveMember)
                .build();
        given(supplementReplyRepository.findById(parent.getId())).willThrow(new EntityNotFoundException("not found SupplementReply : " + parent.getId()));
        //when
        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.getSupplementRepliesByParent(saveSupplement.getId(), parent.getId()),
                "not found SupplementReply : " + parent.getId()
        );
    }


    /**
     * 영양제1
     *      댓글1
     *      댓글2
     *      댓글3
     */
    @Test
    @DisplayName("영양제 댓글 등록")
    public void 영양제_댓글_등록(){

        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyRequestDto supplementReplyRequestDto1 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1"));
        SupplementReplyRequestDto supplementReplyRequestDto2 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply2"));
        SupplementReplyRequestDto supplementReplyRequestDto3 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply3"));

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId()))
                .thenReturn(1L) //처음 호출
                .thenReturn(2L) //두번째 호출
                .thenReturn(3L);

        when(supplementReplyRepository.save(isA(SupplementReply.class)))
                .thenReturn(SupplementReply.builder().groups(1L).content(supplementReplyRequestDto1.getContent()).build())
                .thenReturn(SupplementReply.builder().groups(2L).content(supplementReplyRequestDto2.getContent()).build())
                .thenReturn(SupplementReply.builder().groups(3L).content(supplementReplyRequestDto3.getContent()).build());

        //when
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto1);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto2);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto3);

        //then
        then(supplementRepository).should(times(3)).findById(saveSupplement.getId());
        then(supplementReplyRepository).should(times(3)).save(isA(SupplementReply.class));
        then(supplementReplyRepository).should(times(3)).findByLastOrderWithParent(saveSupplement.getId());
    }
    /**
     *  영양제1
     *          댓글1
     *              댓글2
     *              댓글3
     */
    @Test
    public void 영양제_대댓글_등록_테스트(){

        //given
        SupplementReplyRequestDto supplementReplyRequestDto1 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1"));
        SupplementReplyRequestDto supplementReplyRequestDto2 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1-1"));
        SupplementReplyRequestDto supplementReplyRequestDto3 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1-2"));


        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        //when
        SupplementReply insertSupplement = SupplementReply.builder()
                .id(1L)
                .content(supplementReplyRequestDto1.getContent())
                .groups(1L)
                .groupOrder(1L)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertSupplement);
        when(supplementReplyRepository.findById(insertSupplement.getId())).thenReturn(Optional.of(insertSupplement));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement.getId())).thenReturn(2L).thenReturn(3L);

        SupplementReplyResponseDto saveSupplementReplyResponseDto = supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto1);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReplyResponseDto.getId(), saveMember, supplementReplyRequestDto2);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReplyResponseDto.getId(), saveMember, supplementReplyRequestDto3);

        //then
        then(supplementRepository).should(times(3)).findById(saveSupplement.getId());
        then(supplementReplyRepository).should(times(3)).save(isA(SupplementReply.class));
        then(supplementReplyRepository).should(times(1)).findByLastOrderWithParent(saveSupplement.getId());
        then(supplementReplyRepository).should(times(2)).findByLastOrderWithChild(saveSupplement.getId(), saveSupplementReplyResponseDto.getId());
    }

    /**
     *  영양제1
     *          댓글1
     */
    @Test
    public void 업데이트_댓글(){

        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyRequestDto supplementReplyRequestDto = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1"));


        SupplementReply insertSupplement = SupplementReply.builder()
                .id(1L)
                .content(supplementReplyRequestDto.getContent())
                .groups(1L)
                .groupOrder(1L)
                .member(saveMember)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertSupplement);
        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.findByIdAndMember(anyLong(), isA(Member.class))).thenReturn(Optional.ofNullable(insertSupplement));
        //when
        SupplementReplyResponseDto saveSupplementReplyResponseDto = supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto);
        //when
        supplementReplyRequestDto.setContent("test1(수정)");
        SupplementReplyResponseDto updateSupplementReplyResponseDto = supplementReplyService.updateSupplementReply(saveSupplementReplyResponseDto.getId(), saveMember, supplementReplyRequestDto);
        //then
        Assertions.assertEquals(supplementReplyRequestDto.getContent(), updateSupplementReplyResponseDto.getContent());
        then(supplementRepository).should(times(1)).findById(saveSupplement.getId());
        then(supplementReplyRepository).should(times(1)).save(isA(SupplementReply.class));
        then(supplementReplyRepository).should(times(1)).findByLastOrderWithParent(saveSupplement.getId());
        then(supplementReplyRepository).should(times(1)).findByIdAndMember(insertSupplement.getId(), saveMember);

    }

    /**
     *  영양제1
     *          댓글1
     */
    @Test
    @DisplayName("DEPTH 1 대댓글 없으면 삭제")
    public void 삭제_댓글_한개(){

        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        SupplementReply insertData = SupplementReply.builder()
                                    .id(1L)
                                    .deleteFlag(false)
                                    .member(saveMember)
                                    .parent(null)
                                    .supplement(saveSupplement)
                                    .build();
        when(supplementReplyRepository.findByIdAndMember(anyLong(), isA(Member.class))).thenReturn(Optional.ofNullable(insertData));
        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertData);

        //when
        supplementReplyService.deleteSupplementReply(insertData.getId(), saveMember);

        //then
        then(supplementReplyRepository).should(times(1)).findByIdAndMember(insertData.getId(), saveMember);
        then(supplementReplyRepository).should(times(1)).deleteById(insertData.getId());
    }

    /**
     *  영양제1
     *          댓글1
     *              댓글2
     */
    @Test
    @DisplayName("DEPTH 2 대댓글 삭제")
    public void 삭제_대댓글_삭제_한개(){
        //given
        SupplementReplyRequestDto supplementReplyRequestDto1 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1"));
        SupplementReplyRequestDto supplementReplyRequestDto2 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1-1"));
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        //when
        SupplementReply insertSupplement1 = SupplementReply.builder()
                .id(1L)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        SupplementReply insertSupplement2 = SupplementReply.builder()
                .id(2L)
                .deleteFlag(false)
                .parent(insertSupplement1)
                .supplement(saveSupplement)
                .build();

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertSupplement1).thenReturn(insertSupplement2);
        when(supplementReplyRepository.findById(insertSupplement1.getId())).thenReturn(Optional.of(insertSupplement1));
        when(supplementReplyRepository.findById(insertSupplement2.getId())).thenReturn(Optional.of(insertSupplement2));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement1.getId())).thenReturn(2L);
        given(supplementReplyRepository.findByIdAndMember(insertSupplement1.getId(),saveMember)).willReturn(Optional.of(insertSupplement1));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement2.getId(),saveMember)).willReturn(Optional.of(insertSupplement2));

        SupplementReplyResponseDto parentReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto1);
        SupplementReplyResponseDto childReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), parentReply.getId(), saveMember, supplementReplyRequestDto2);
        supplementReplyService.deleteSupplementReply(childReply.getId(), saveMember);

        //then
        then(supplementReplyRepository).should( times(1)).deleteById(childReply.getId());
    }

    /**
     *  영양제1
     *          댓글1
     *              댓글2
     */
    @Test
    @DisplayName("DEPTH 2 대댓글, DEPTH 1 댓글 flag")
    public void 삭제_댓글_대댓글존재(){

        //given
        SupplementReplyRequestDto supplementReplyRequestDto1 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1"));
        SupplementReplyRequestDto supplementReplyRequestDto2 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1-1"));
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        //when
        SupplementReply insertSupplement1 = SupplementReply.builder()
                .id(1L)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        SupplementReply insertSupplement2 = SupplementReply.builder()
                .id(2L)
                .deleteFlag(false)
                .parent(insertSupplement1)
                .supplement(saveSupplement)
                .build();

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertSupplement1).thenReturn(insertSupplement2);
        when(supplementReplyRepository.findById(insertSupplement1.getId())).thenReturn(Optional.of(insertSupplement1));
        when(supplementReplyRepository.findById(insertSupplement2.getId())).thenReturn(Optional.of(insertSupplement2));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement1.getId(),saveMember)).willReturn(Optional.of(insertSupplement1));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement2.getId(),saveMember)).willReturn(Optional.of(insertSupplement2));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement1.getId())).thenReturn(2L);

        SupplementReplyResponseDto parentReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto1);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), parentReply.getId(), saveMember, supplementReplyRequestDto2);

        supplementReplyService.deleteSupplementReply(parentReply.getId(), saveMember);

        //then
        then(supplementReplyRepository).should( times(0)).deleteById(parentReply.getId());
        Assertions.assertTrue(insertSupplement1.getDeleteFlag());
    }


    /**
     *  영양제1
     *          댓글1
     *              댓글2
     */
    @Test
    @DisplayName("대댓글을 지웠을때, 부모도 삭제상태이고,대댓글을 마지막이 나였다면 부모도 지울때, (DEPTH 2 대댓글 삭제, DEPTH 1 댓글 삭제Flag(true) 상황에서 모두 지움)")
    public void 삭제_댓글_All(){

        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyRequestDto supplementReplyRequestDto1 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1"));
        SupplementReplyRequestDto supplementReplyRequestDto2 = new SupplementReplyRequestDto(new SupplementReplyRequest("testReply1-1"));


        //when
        SupplementReply insertSupplement1 = SupplementReply.builder()
                .id(1L)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        SupplementReply insertSupplement2 = SupplementReply.builder()
                .id(2L)
                .deleteFlag(false)
                .parent(insertSupplement1)
                .supplement(saveSupplement)
                .build();

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertSupplement1).thenReturn(insertSupplement2);
        when(supplementReplyRepository.findById(insertSupplement1.getId())).thenReturn(Optional.of(insertSupplement1));
        when(supplementReplyRepository.findById(insertSupplement2.getId())).thenReturn(Optional.of(insertSupplement2));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement1.getId(),saveMember)).willReturn(Optional.of(insertSupplement1));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement2.getId(),saveMember)).willReturn(Optional.of(insertSupplement2));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement1.getId())).thenReturn(2L);

        SupplementReplyResponseDto parentReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), saveMember, supplementReplyRequestDto1);
        SupplementReplyResponseDto childReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), parentReply.getId(), saveMember, supplementReplyRequestDto2);

        supplementReplyService.deleteSupplementReply(parentReply.getId(), saveMember);
        Assertions.assertTrue(insertSupplement1.getDeleteFlag());
        supplementReplyService.deleteSupplementReply(childReply.getId(), saveMember);

        //then
        then(supplementReplyRepository).should(times(2)).deleteById(anyLong());

    }

}