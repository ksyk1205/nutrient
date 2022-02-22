package mandykr.nutrient.service.supplement;

import mandykr.nutrient.dto.supplement.reply.SupplementReplyDto;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import mandykr.nutrient.repository.supplement.reply.SupplementReplyRepository;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayName("SupplementReplyServiceTest")
class SupplementReplyServiceTest {

    SupplementReplyRepository supplementReplyRepository = mock(SupplementReplyRepository.class);

    SupplementRepository supplementRepository =  mock(SupplementRepository.class);

    SupplementReplyService supplementReplyService = new SupplementReplyService(supplementReplyRepository,supplementRepository);

    final static int PAGE = 1;
    final static int PAGE_SIZE = 2;
    Supplement supplement;
    Member member;
    SupplementReply parent1;
    SupplementReply child1_1;
    SupplementReply child1_2;
    SupplementReply parent2;
    List<SupplementReply> parentList = new ArrayList<>();
    List<SupplementReply> childList = new ArrayList<>();
    Pageable pageRequest;
    Page<SupplementReply> parentReplyPageResult;
    Page<SupplementReply> childReplyPageResult;

    SupplementReplyRequest supplementReplyRequest;

    @BeforeEach
    void setup(){
        this.supplement = Supplement.builder().id(1L).name("test1").ranking(4.2).build();

        Member member = new Member();
        //member.setId(1L);
        //member.setMemberId("testMember");
        //member.setName("KIM");
        this.member = member;

        parent1 = makeParent(1L, "TEST1", 1L, 1L);
        child1_1 = makeChild(2L, "TEST1-1", 1L, 2L, parent1);
        child1_2 = makeChild(3L, "TEST1-2", 1L, 2L, parent1);
        parent2 = makeParent(4L, "TEST2", 2L, 1L);
        parentList.add(parent1);
        childList.add(child1_1);
        childList.add(child1_2);
        parentList.add(parent2);

        pageRequest = new PageRequestUtil(PAGE, PAGE_SIZE).getPageable();
        parentReplyPageResult = new PageImpl<>(parentList, pageRequest, parentList.size());
        childReplyPageResult = new PageImpl<>(childList, pageRequest, childList.size());
        supplementReplyRequest = new SupplementReplyRequest("TEST");
    }

    private SupplementReply makeChild(Long id, String content, Long groups, Long groupOrder, SupplementReply parent) {
        return SupplementReply.builder()
                .id(id)
                .content(content)
                .groups(groups)
                .groupOrder(groupOrder)
                .member(member)
                .deleted(false)
                .parent(parent)
                .supplement(supplement)
                .build();
    }

    private SupplementReply makeParent(Long id, String content, Long groups, Long groupOrder) {
        return SupplementReply.builder()
                .id(id)
                .content(content)
                .groups(groups)
                .groupOrder(groupOrder)
                .member(member)
                .deleted(false)
                .parent(null)
                .supplement(supplement)
                .build();
    }

    private void isEqualTo(SupplementReplyDto expect, SupplementReply actually) {
        Long actuallyParent = actually.getParent() == null ? null : actually.getParent().getId();
        assertThat(expect.getId()).isEqualTo(actually.getId());
        assertThat(expect.getContent()).isEqualTo(actually.getContent());
        assertThat(expect.getGroups()).isEqualTo(actually.getGroups());
        assertThat(expect.getGroupOrder()).isEqualTo(actually.getGroupOrder());
        assertThat(expect.getDeleted()).isEqualTo(actually.getDeleted());
        assertThat(expect.getSupplement()).isEqualTo(actually.getSupplement().getId());
        assertThat(expect.getParent()).isEqualTo(actuallyParent);
    }

    @Test
    @DisplayName("영양제 부모 댓글 보기")
    public void 영양제_부모_댓글_보기(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        given(supplementReplyRepository.findBySupplementWithParent(supplement, pageRequest))
                .willReturn(parentReplyPageResult);
        Page<SupplementReplyDto> page = supplementReplyService.getSupplementRepliesWithParent(supplement.getId(), pageRequest);

        //then
        assertThat(page.getSize()).isEqualTo(2);
        List<SupplementReplyDto> content = page.getContent();
        for(int i=0;i<content.size();++i){
            isEqualTo(content.get(i), parentList.get(i));
        }
    }




    @Test
    @DisplayName("영양제 부모 댓글 보기(영양제 없음)")
    public void 영양제_부모_댓글_영양제_없음(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId()))
                .thenThrow(new EntityNotFoundException("not found Supplement : " + supplement.getId()));
        //then
        assertThrows(EntityNotFoundException.class,
                () -> supplementReplyService.getSupplementRepliesWithParent(supplement.getId(), pageRequest),
                "not found Supplement : " + supplement.getId());
    }


    @Test
    @DisplayName("영양제 자식 댓글 보기")
    public void 영양제_자식_댓글_보기(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(supplementReplyRepository.findById(parent1.getId())).thenReturn(Optional.of(parent1));
        when(supplementReplyRepository.findBySupplementWithChild(supplement, parent1, pageRequest)).thenReturn(childReplyPageResult);
        Page<SupplementReplyDto> page = supplementReplyService.getSupplementRepliesWithChild(supplement.getId(), parent1.getId(), pageRequest);

        //then
        assertThat(page.getSize()).isEqualTo(2);
        List<SupplementReplyDto> content = page.getContent();
        for(int i=0;i<content.size();++i){
            isEqualTo(content.get(i), childList.get(i));
        }
    }



    @Test
    @DisplayName("영양제 자식 댓글 보기(영양제 없음)")
    public void 영양제_자식_댓글_영양제_없음(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId()))
                .thenThrow(new EntityNotFoundException("not found Supplement : " + supplement.getId()));

        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.getSupplementRepliesWithChild(supplement.getId(), parent1.getId(), pageRequest),
                "not found Supplement : " + supplement.getId()
        );
    }


    @Test
    @DisplayName("영양제 자식 댓글 보기(부모 댓글 없음)")
    public void 영양제_자식_댓글_부모_댓글_없음(){
        //given

        //when
        when(supplementReplyRepository.findById(parent1.getId()))
                .thenThrow(new EntityNotFoundException("not found SupplementReply : " + parent1.getId()));

        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.getSupplementRepliesWithChild(supplement.getId(), parent1.getId(), pageRequest),
                "not found SupplementReply : " + parent1.getId()
        );
    }



    @Test
    @DisplayName("영양제 댓글 등록(부모 댓글)")
    public void 영양제_댓글_등록(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(supplementReplyRepository.findByParentLastGroup(supplement.getId())).thenReturn(null);
        when(supplementReplyRepository.save(any(SupplementReply.class))).thenReturn(parent1);
        SupplementReplyDto supplementReply = supplementReplyService.createSupplementReply(supplement.getId(), member, supplementReplyRequest);

        //then
        isEqualTo(supplementReply, parent1);
    }

    @Test
    @DisplayName("영양제 댓글 등록(부모 댓글2)")
    public void 영양제_댓글_등록_2번쨰부모(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(supplementReplyRepository.findByParentLastGroup(supplement.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(any(SupplementReply.class))).thenReturn(parent2);
        SupplementReplyDto supplementReply = supplementReplyService.createSupplementReply(supplement.getId(), member, supplementReplyRequest);

        //then
        isEqualTo(supplementReply, parent2);
    }

    @Test
    @DisplayName("영양제 댓글 등록(영양제 없음)")
    public void 영양제_댓글_등록_영양제_없음(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenThrow(new EntityNotFoundException("not found Supplement : " + supplement.getId()));

        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.createSupplementReply(supplement.getId(), member, supplementReplyRequest),
                "not found Supplement : " + supplement.getId()
        );
    }


    @Test
    @DisplayName("영양제 대댓글 등록(자식 댓글)")
    public void 영양제_대댓글_등록_자식_댓글_처음(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(supplementReplyRepository.findById(parent1.getId())).thenReturn(Optional.of(parent1));
        when(supplementReplyRepository.findByChildLastGroupOrder(supplement.getId(), parent1.getId())).thenReturn(null);
        when(supplementReplyRepository.save(any(SupplementReply.class))).thenReturn(child1_1);
        SupplementReplyDto supplementReply = supplementReplyService.createSupplementReply(supplement.getId(), parent1.getId(), member, supplementReplyRequest);

        //then
        isEqualTo(supplementReply, child1_1);
    }

    @Test
    @DisplayName("영양제 대댓글 등록(자식 댓글_있음)")
    public void 영양제_대댓글_등록_자식_댓글_두번쨰(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(supplementReplyRepository.findById(parent1.getId())).thenReturn(Optional.of(parent1));
        when(supplementReplyRepository.findByChildLastGroupOrder(supplement.getId(), parent1.getId())).thenReturn(1L);
        when(supplementReplyRepository.save(any(SupplementReply.class))).thenReturn(child1_2);
        SupplementReplyDto supplementReply = supplementReplyService.createSupplementReply(supplement.getId(), parent1.getId(), member, supplementReplyRequest);

        //then
        isEqualTo(supplementReply, child1_2);
    }

    @Test
    @DisplayName("영양제 대댓글 등록(영양제 없음)")
    public void 영양제_대댓글_등록_영양제_없음(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenThrow(
                new EntityNotFoundException("not found Supplement : " + supplement.getId()));

        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.createSupplementReply(supplement.getId(), member, supplementReplyRequest),
                "not found Supplement : " + supplement.getId()
        );
    }


    @Test
    @DisplayName("영양제 댓글 등록(댓글 없음)")
    public void 영양제_대댓글_등록_댓글_없음(){
        //given

        //when
        when(supplementReplyRepository.findById(parent1.getId()))
                .thenThrow(new EntityNotFoundException("not found SupplementReply : " + parent1.getId()));

        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.createSupplementReply(supplement.getId(), member, supplementReplyRequest),
                "not found SupplementReply : " + parent1.getId()
        );
    }


    @Test
    @DisplayName("댓글 부모 수정")
    public void 댓글_수정_부모(){

        //given
        supplementReplyRequest.setContent("test1(수정)");
        when(supplementReplyRepository.findByIdAndMember(parent1.getId(), member)).thenReturn(Optional.of(parent1));
        SupplementReplyDto result = supplementReplyService.updateSupplementReply(parent1.getId(), member, supplementReplyRequest);
        
        //then
        isEqualTo(result, parent1);
    }

    @Test
    @DisplayName("댓글 자식 수정")
    public void 댓글_수정_자식(){

        //given
        supplementReplyRequest.setContent("test1(수정)");
        when(supplementReplyRepository.findByIdAndMember(child1_1.getId(), member)).thenReturn(Optional.of(child1_1));
        SupplementReplyDto result = supplementReplyService.updateSupplementReply(child1_1.getId(), member, supplementReplyRequest);

        //then
        isEqualTo(result, child1_1);
    }

    @Test
    @DisplayName("댓글 수정 (Member,Id 해당되는 데이터 X)")
    public void 댓글_수정_자식_Memeber_조회_데이터X(){

        //given
        supplementReplyRequest.setContent("test1(수정)");
        when(supplementReplyRepository.findByIdAndMember(child1_1.getId(), member))
                .thenThrow(new EntityNotFoundException("not found SupplementReply : " + child1_1.getId()));

        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.updateSupplementReply(child1_1.getId(), member, supplementReplyRequest),
                "not found SupplementReply : " + child1_1.getId()
        );
    }

    @Test
    @DisplayName("부모 댓글 삭제 (대댓글 없을경우)")
    public void 부모_댓글_삭제_대댓글없을경우(){
        //given

        //when
        when(supplementReplyRepository.findByIdAndMember(parent2.getId(), member)).thenReturn(Optional.ofNullable(parent2));
        supplementReplyService.deleteSupplementReply(parent2.getId(), member);

        //then
        then(supplementReplyRepository).should(times(1))
                .deleteById(parent2.getId());
    }

    @Test
    @DisplayName("부모 댓글 삭제 (대댓글 있을경우)")
    public void 부모_댓글_삭제_대댓글있을경우(){
        //given
        child1_1.addParents(parent1);
        child1_2.addParents(parent1);

        //when
        when(supplementReplyRepository.findByIdAndMember(parent1.getId(), member)).thenReturn(Optional.ofNullable(parent1));
        supplementReplyService.deleteSupplementReply(parent1.getId(), member);

        //then
        assertTrue(parent1.getDeleted());
    }

    @Test
    @DisplayName("자식 댓글 삭제(다른 자식 댓글들이 남아잇다)")
    public void 자식_댓글_삭제_다른자식댓글들존재(){
        //given
        child1_1.addParents(parent1);
        child1_2.addParents(parent1);

        //when
        when(supplementReplyRepository.findByIdAndMember(child1_1.getId(), member))
                .thenReturn(Optional.ofNullable(child1_1));
        supplementReplyService.deleteSupplementReply(child1_1.getId(), member);

        //then
        then(supplementReplyRepository).should(times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("자식 댓글 삭제(부모 댓글이 삭제상태일 경우)")
    public void 자식_댓글_삭제_부모댓글이삭제상태(){
        //given
        parent1.delete();
        child1_1.addParents(parent1);


        //when
        when(supplementReplyRepository.findByIdAndMember(child1_1.getId(), member))
                .thenReturn(Optional.ofNullable(child1_1));
        supplementReplyService.deleteSupplementReply(child1_1.getId(), member);

        //then
        then(supplementReplyRepository).should(times(2)).deleteById(anyLong());

    }

    @Test
    @DisplayName("내 댓글이 아닐떄 삭제할 경우")
    public void 내_댓글_아닐떄_삭제(){
        //given
        Member member2 = new Member();
        //member2.setId(2L);
        //member2.setMemberId("Member2");
        //member2.setName("bro");

        //when
        when(supplementReplyRepository.findByIdAndMember(child1_1.getId(), member2))
                .thenThrow(new EntityNotFoundException("not found SupplementReply : " + child1_1.getId()));

        //then
        assertThrows(
                EntityNotFoundException.class,
                () -> supplementReplyService.deleteSupplementReply(child1_1.getId(), member2),
                "not found SupplementReply : " + child1_1.getId()
        );

    }
}