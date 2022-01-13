package mandykr.nutrient.service.supplement;

import mandykr.nutrient.dto.supplement.reply.SupplementReplyDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import mandykr.nutrient.repository.supplement.reply.SupplementReplyRepository;
import mandykr.nutrient.repository.SupplementRepository;
import mandykr.nutrient.service.supplement.SupplementReplyService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplementReplyServiceTest")
class SupplementReplyServiceTest {

    SupplementReplyRepository supplementReplyRepository = mock(SupplementReplyRepository.class);

    SupplementRepository supplementRepository =  mock(SupplementRepository.class);

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
        SupplementReplyDto supplementReplyDto1 = SupplementReplyDto.toSupplementReplyDto("testReply1");
        SupplementReplyDto supplementReplyDto2 = SupplementReplyDto.toSupplementReplyDto("testReply2");
        SupplementReplyDto supplementReplyDto3 = SupplementReplyDto.toSupplementReplyDto("testReply3");

        when(supplementReplyRepository.findByLastOrderWithParent(saveSupplement.getId()))
                .thenReturn(1L) //처음 호출
                .thenReturn(2L) //두번째 호출
                .thenReturn(3L);

        when(supplementReplyRepository.save(isA(SupplementReply.class)))
                .thenReturn(SupplementReply.builder().groups(1L).content(supplementReplyDto1.getContent()).build())
                .thenReturn(SupplementReply.builder().groups(2L).content(supplementReplyDto2.getContent()).build())
                .thenReturn(SupplementReply.builder().groups(3L).content(supplementReplyDto3.getContent()).build());

        //when
        SupplementReplyDto saveSupplementReplyDto1 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyDto1);
        SupplementReplyDto saveSupplementReplyDto2 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyDto2);
        SupplementReplyDto saveSupplementReplyDto3 = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyDto3);

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
        SupplementReplyDto supplementReplyDto1 = SupplementReplyDto.toSupplementReplyDto("testReply1");
        SupplementReplyDto supplementReplyDto2 = SupplementReplyDto.toSupplementReplyDto("testReply1-1");
        SupplementReplyDto supplementReplyDto3 = SupplementReplyDto.toSupplementReplyDto("testReply1-2");

        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));


        //when
        SupplementReply insertSupplement = SupplementReply.builder()
                .id(1L)
                .content(supplementReplyDto1.getContent())
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
        SupplementReplyDto saveSupplementReplyDto = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyDto1);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReplyDto.getId(), member, supplementReplyDto2);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), saveSupplementReplyDto.getId(), member, supplementReplyDto3);

        //then
        then(supplementRepository).should(times(3)).findById(saveSupplement.getId());
        then(supplementReplyRepository).should(times(3)).save(isA(SupplementReply.class));
        then(supplementReplyRepository).should(times(1)).findByLastOrderWithParent(saveSupplement.getId());
        then(supplementReplyRepository).should(times(2)).findByLastOrderWithChild(saveSupplement.getId(),saveSupplementReplyDto.getId());
    }


    @Test
    public void 업데이트_댓글() throws Exception{
        /**
         *  영양제1
         *          댓글1
         */
        //given
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyDto supplementReplyDto = SupplementReplyDto.toSupplementReplyDto("testReply1");


        SupplementReply insertSupplement = SupplementReply.builder()
                .id(1L)
                .content(supplementReplyDto.getContent())
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
        SupplementReplyDto saveSupplementReplyDto = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyDto);
        //when
        supplementReplyDto.setContent("test1(수정)");
        SupplementReplyDto updateSupplementReplyDto = supplementReplyService.updateSupplementReply(saveSupplementReplyDto.getId(), member, supplementReplyDto);
        //then
        Assertions.assertEquals(supplementReplyDto.getContent(),updateSupplementReplyDto.getContent());
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
        SupplementReplyDto supplementReplyDto1 = SupplementReplyDto.toSupplementReplyDto("testReply1");
        SupplementReplyDto supplementReplyDto2 = SupplementReplyDto.toSupplementReplyDto("testReply1-1");
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
        SupplementReplyDto parentReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyDto1);
        SupplementReplyDto childReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), parentReply.getId(), member,supplementReplyDto2);

        //when
        supplementReplyService.deleteSupplementReply(childReply.getId(), member);

        //then
        then(supplementReplyRepository).should( times(1)).deleteById(childReply.getId());
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
        SupplementReplyDto supplementReplyDto1 = SupplementReplyDto.toSupplementReplyDto("testReply1");
        SupplementReplyDto supplementReplyDto2 = SupplementReplyDto.toSupplementReplyDto("testReply1-1");
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
        SupplementReplyDto parentReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), member,supplementReplyDto1);
        supplementReplyService.createSupplementReply(saveSupplement.getId(), parentReply.getId(), member, supplementReplyDto2);

        //when
        supplementReplyService.deleteSupplementReply(parentReply.getId(), member);

        //then
        then(supplementReplyRepository).should( times(0)).deleteById(parentReply.getId());
        Assertions.assertTrue(insertSupplement1.getDeleteFlag());
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
        SupplementReplyDto supplementReplyDto1 = SupplementReplyDto.toSupplementReplyDto("testReply1");
        SupplementReplyDto supplementReplyDto2 = SupplementReplyDto.toSupplementReplyDto("testReply1-1");

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

        SupplementReplyDto parentReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), member, supplementReplyDto1);
        SupplementReplyDto childReply = supplementReplyService.createSupplementReply(saveSupplement.getId(), parentReply.getId(), member, supplementReplyDto2);

        //when
        supplementReplyService.deleteSupplementReply(parentReply.getId(), member);
        Assertions.assertTrue(insertSupplement1.getDeleteFlag());
        supplementReplyService.deleteSupplementReply(childReply.getId(), member);

        //then
        then(supplementReplyRepository).should(times(2)).deleteById(anyLong());

    }

}