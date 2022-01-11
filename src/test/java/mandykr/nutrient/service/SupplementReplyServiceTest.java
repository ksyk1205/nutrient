package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementReplyRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplementReplyServiceTest")
class SupplementReplyServiceTest {

    SupplementReplyRepository supplementReplyRepository = mock(SupplementReplyRepository.class);

    SupplementRepository supplementRepository = mock(SupplementRepository.class);;

    SupplementReplyService supplementReplyService = new SupplementReplyService(supplementReplyRepository,supplementRepository);

    Supplement saveSupplement;

    Member member;

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
        this.member = member;
    }

    @Test
    public void 영양제_댓글_등록_테스트() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *          댓글2
         *          댓글3
         */
        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId()))
                .thenReturn(1L) //처음 호출
                .thenReturn(2L) //두번째 호출
                .thenReturn(3L);

        when(supplementReplyRepository.save(isA(SupplementReply.class)))
                .thenReturn(SupplementReply.builder().groups(1L).content(supplementReplyRequest1.getContent()).build())
                .thenReturn(SupplementReply.builder().groups(2L).content(supplementReplyRequest2.getContent()).build())
                .thenReturn(SupplementReply.builder().groups(3L).content(supplementReplyRequest3.getContent()).build());

        //when
        SupplementReply supplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyRequest1).get();
        SupplementReply supplementReply2 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyRequest2).get();
        SupplementReply supplementReply3 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyRequest3).get();

        //then
        then(supplementRepository).should(times(3)).findById(saveSupplement.getId());
        then(supplementReplyRepository).should(times(3)).save(isA(SupplementReply.class));
        then(supplementReplyRepository).should(times(3)).findByLastOrderWithParent(saveSupplement.getId());
    }

    @Test
    public void 영양제_대댓글_등록_테스트() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         */
        //given
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply1-1");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply1-2");
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));


        //when
        SupplementReply insertSupplement = SupplementReply.builder()
                .id(1L)
                .content(supplementReplyRequest1.getContent())
                .groups(1L)
                .groupOrder(1L)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertSupplement);
        when(supplementReplyRepository.findById(insertSupplement.getId())).thenReturn(Optional.ofNullable(insertSupplement));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement.getId())).thenReturn(2L).thenReturn(3L);

        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyRequest1).get();
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReply1.getId(), member, supplementReplyRequest2);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReply1.getId(), member, supplementReplyRequest3);

        //then
        then(supplementRepository).should(times(3)).findById(saveSupplement.getId());
        then(supplementReplyRepository).should(times(3)).save(isA(SupplementReply.class));
        then(supplementReplyRepository).should(times(1)).findByLastOrderWithParent(saveSupplement.getId());
        then(supplementReplyRepository).should(times(2)).findByLastOrderWithChild(saveSupplement.getId(),saveSupplementReply1.getId());


    }


    @Test
    public void 업데이트_댓글() throws Exception{
        /**
         *  영양제1
         *          댓글1
         */
        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyRequest supplementReplyRequest = new SupplementReplyRequest();
        supplementReplyRequest.setContent("testReply1");

        SupplementReply insertSupplement = SupplementReply.builder()
                .id(1L)
                .content(supplementReplyRequest.getContent())
                .groups(1L)
                .groupOrder(1L)
                .member(member)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertSupplement);
        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.findByIdAndMember(anyLong(), isA(Member.class))).thenReturn(Optional.ofNullable(insertSupplement));
        //when
        SupplementReply saveSupplementReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyRequest).get();
        //when
        supplementReplyRequest.setContent("test1(수정)");
        SupplementReply supplementReply = supplementReplyService.updateSupplementReply(saveSupplementReply.getId(), member, supplementReplyRequest).get();
        //then
        Assertions.assertEquals(supplementReplyRequest.getContent(),supplementReply.getContent());
        then(supplementRepository).should(times(1)).findById(saveSupplement.getId());
        then(supplementReplyRepository).should(times(1)).save(isA(SupplementReply.class));
        then(supplementReplyRepository).should(times(1)).findByLastOrderWithParent(saveSupplement.getId());
        then(supplementReplyRepository).should(times(1)).findByIdAndMember(insertSupplement.getId(), member);

    }

    /**
     *  DEPTH 1 대댓글 없으면 삭제
     * @throws Exception
     */
    @Test
    public void 삭제_댓글_한개() throws Exception{
        /**
         *  영양제1
         *          댓글1
         */
        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        SupplementReplyRequest supplementReplyRequest = new SupplementReplyRequest();
        supplementReplyRequest.setContent("testReply1");
        SupplementReply insertData = SupplementReply.builder()
                                    .id(1L)
                                    .deleteFlag(false)
                                    .member(member)
                                    .parent(null)
                                    .supplement(saveSupplement)
                                    .build();
        when(supplementReplyRepository.findByIdAndMember(anyLong(), isA(Member.class))).thenReturn(Optional.ofNullable(insertData));
        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(isA(SupplementReply.class))).thenReturn(insertData);

        //when
        supplementReplyService.deleteSupplementReply(insertData.getId(), member);

        //then
        then(supplementReplyRepository).should(times(1)).findByIdAndMember(insertData.getId(), member);
        then(supplementReplyRepository).should(times(1)).deleteById(insertData.getId());
    }

    /**
     *  DEPTH 2 대댓글 삭제
     * @throws Exception
     */
    @Test
    public void 삭제_대댓글_삭제_한개() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         */
        //given
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply1-1");
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
        when(supplementReplyRepository.findById(insertSupplement1.getId())).thenReturn(Optional.ofNullable(insertSupplement1));
        when(supplementReplyRepository.findById(insertSupplement2.getId())).thenReturn(Optional.ofNullable(insertSupplement2));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement1.getId())).thenReturn(2L);
        given(supplementReplyRepository.findByIdAndMember(insertSupplement1.getId(),member)).willReturn(Optional.ofNullable(insertSupplement1));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement2.getId(),member)).willReturn(Optional.ofNullable(insertSupplement2));

        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyRequest1).get();
        SupplementReply supplementReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReply1.getId(), member,supplementReplyRequest2).get();

        //when
        supplementReplyService.deleteSupplementReply(supplementReply.getId(), member);

        //then
        then(supplementReplyRepository).should( times(1)).deleteById(supplementReply.getId());
    }

    /**
     *  DEPTH 2 대댓글
     *  DEPTH 1 댓글 flag
     * @throws Exception
     */
    @Test
    @DisplayName("대댓글있는 댓글 삭제(flag만 변경)")
    public void 삭제_댓글_대댓글존재() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         */
        //given
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply1-1");
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
        when(supplementReplyRepository.findById(insertSupplement1.getId())).thenReturn(Optional.ofNullable(insertSupplement1));
        when(supplementReplyRepository.findById(insertSupplement2.getId())).thenReturn(Optional.ofNullable(insertSupplement2));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement1.getId(),member)).willReturn(Optional.ofNullable(insertSupplement1));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement2.getId(),member)).willReturn(Optional.ofNullable(insertSupplement2));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement1.getId())).thenReturn(2L);

        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member,supplementReplyRequest1).get();
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReply1.getId(), member, supplementReplyRequest2).get();

        //when
        supplementReplyService.deleteSupplementReply(saveSupplementReply1.getId(), member);

        //then
        then(supplementReplyRepository).should( times(0)).deleteById(saveSupplementReply1.getId());
        Assertions.assertTrue(saveSupplementReply1.getDeleteFlag());
    }

    /**
     * 대댓글을 지웠을때, 부모도 삭제상태이고,대댓글을 마지막이 나였다면 부모도 지울때
     *  DEPTH 2 대댓글 삭제
     *  DEPTH 1 댓글 flag
     *  모두 지움
     * @throws Exception
     */
    @Test
    public void 삭제_댓글_All() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         */
        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply1-1");

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
        when(supplementReplyRepository.findById(insertSupplement1.getId())).thenReturn(Optional.ofNullable(insertSupplement1));
        when(supplementReplyRepository.findById(insertSupplement2.getId())).thenReturn(Optional.ofNullable(insertSupplement2));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement1.getId(),member)).willReturn(Optional.ofNullable(insertSupplement1));
        given(supplementReplyRepository.findByIdAndMember(insertSupplement2.getId(),member)).willReturn(Optional.ofNullable(insertSupplement2));
        when(supplementReplyRepository.findByLastOrderWithChild(saveSupplement.getId(),insertSupplement1.getId())).thenReturn(2L);

        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReply1.getId(), member, supplementReplyRequest2).get();

        //when
        supplementReplyService.deleteSupplementReply(saveSupplementReply1.getId(), member);
        Assertions.assertTrue(saveSupplementReply1.getDeleteFlag());
        supplementReplyService.deleteSupplementReply(saveSupplementReply2.getId(), member);

        //then
        then(supplementReplyRepository).should(times(2)).deleteById(anyLong());

    }

}