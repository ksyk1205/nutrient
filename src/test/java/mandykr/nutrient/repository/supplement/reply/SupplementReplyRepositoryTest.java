package mandykr.nutrient.repository.supplement.reply;

import mandykr.nutrient.config.TestConfig;
import mandykr.nutrient.entity.member.Member;

import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import mandykr.nutrient.repository.member.MemberRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.by;

@DataJpaTest //JPA 테스트
@Import(TestConfig.class)
class SupplementReplyRepositoryTest {
    @Autowired
    SupplementReplyRepository supplementReplyRepository;

    @Autowired
    SupplementRepository supplementRepository;


    @Autowired
    MemberRepository memberRepository;

    //미리 주입할 데이터
    final static int PAGE = 1;
    final static int PAGE_SIZE = 2;

    SupplementReply parent1;
    SupplementReply child1_1;
    SupplementReply child1_2;
    SupplementReply parent2;
    SupplementReply child2_1;
    Supplement supplement;
    Member member;

    List<SupplementReply> parentList = new ArrayList<>();
    List<SupplementReply> childList = new ArrayList<>();
    Pageable pageRequest;
    Page<SupplementReply> parentReplyPageResult;
    Page<SupplementReply> childReplyPageResult;

    @BeforeEach
    public void setup(){
        supplement = supplementRepository.save(Supplement.builder().name("test1").build());
        //Member member = new Member();
        //member.setMemberId("testMemberId1");
        //member.setName("martin");
        //this.member =  memberRepository.save(member);

        parent1 = makeParent("TEST1", 1L, 1L);
        child1_1 = makeChild("TEST1_1", 1L, 2L, parent1);
        child1_2 = makeChild("TEST1_1", 1L, 3L, parent1);
        parent2 = makeParent("TEST2", 2L, 1L);
        child2_1 = makeChild("TEST2_1", 2L, 2L, parent2);

        parentList.add(parent1);
        parentList.add(parent2);

        childList.add(child1_1);
        childList.add(child1_2);

        pageRequest = new PageRequestUtil(PAGE, PAGE_SIZE).getPageable(Sort.by("groupOrder"));
        parentReplyPageResult = new PageImpl<>(parentList, pageRequest, parentList.size());
        childReplyPageResult = new PageImpl<>(childList, pageRequest, childList.size());
    }

    private SupplementReply makeChild(String content, Long groups, Long groupOrder, SupplementReply parent) {
        return SupplementReply.builder()
                .content(content)
                .groups(groups)
                .groupOrder(groupOrder)
                .member(member)
                .deleted(false)
                .parent(parent)
                .supplement(supplement)
                .build();
    }

    private SupplementReply makeParent(String content, Long groups, Long groupOrder) {
        return SupplementReply.builder()
                .content(content)
                .groups(groups)
                .groupOrder(groupOrder)
                .member(member)
                .deleted(false)
                .parent(null)
                .supplement(supplement)
                .build();
    }

    private void isEqualTo(SupplementReply expect, SupplementReply actually) {
        assertThat(expect.getId()).isEqualTo(actually.getId());
        assertThat(expect.getContent()).isEqualTo(actually.getContent());
        assertThat(expect.getGroups()).isEqualTo(actually.getGroups());
        assertThat(expect.getGroupOrder()).isEqualTo(actually.getGroupOrder());
        assertThat(expect.getDeleted()).isEqualTo(actually.getDeleted());
        assertThat(expect.getSupplement()).isEqualTo(actually.getSupplement());
        assertThat(expect.getParent()).isEqualTo(actually.getParent());
    }


    @Test
    public void 댓글_등록() {
        //given

        //when
        SupplementReply saveParent = supplementReplyRepository.save(parent1);
        SupplementReply saveChild = supplementReplyRepository.save(child1_1);

        //then
        isEqualTo(saveParent, supplementReplyRepository.findById(saveParent.getId()).get());
        isEqualTo(saveChild, supplementReplyRepository.findById(saveChild.getId()).get());
    }



    @Test
    public void 댓글_수정(){
        //given
        SupplementReply saveParent = supplementReplyRepository.save(parent1);

        //when
        saveParent.changeContent("UPDATE_TEST");

        //then
        isEqualTo(saveParent, supplementReplyRepository.findById(saveParent.getId()).get());
    }



    @Test
    @DisplayName("댓글 조회(부모만)")
    public void 댓글_영양제로_부모만_조회() {
        //given
        SupplementReply saveParent2 = supplementReplyRepository.save(parent2);
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);

        //when
        Page<SupplementReply> supplementReplies = supplementReplyRepository.findBySupplementWithParent(supplement, pageRequest);

        //then
        assertThat(supplementReplies.getSize()).isEqualTo(2);
        isEqualTo(supplementReplies.getContent().get(0),
                supplementReplyRepository.findById(saveParent1.getId()).get());
        isEqualTo(supplementReplies.getContent().get(1),
                supplementReplyRepository.findById(saveParent2.getId()).get());
    }



    @Test
    @DisplayName("대댓글 영양제,부모댓글로 대댓글 조회")
    public void 댓글_부모로_자식댓글_조회() {
        //given
        supplementReplyRepository.save(parent1);
        SupplementReply saveSupplementReply1 = supplementReplyRepository.save(child1_1);
        SupplementReply saveSupplementReply2 = supplementReplyRepository.save(child1_2);

        //when
        Page<SupplementReply> supplementReplies = supplementReplyRepository.findBySupplementWithChild(supplement, parent1, pageRequest);

        //then
        assertThat(supplementReplies.getSize()).isEqualTo(2);
        isEqualTo(supplementReplies.getContent().get(0),
                supplementReplyRepository.findById(saveSupplementReply1.getId()).get());
        isEqualTo(supplementReplies.getContent().get(1),
                supplementReplyRepository.findById(saveSupplementReply2.getId()).get());

    }




    @Test
    @DisplayName("부모 GroupOrder 최댓값 조회하기")
    public void 부모_댓글_groupOrder_최대값_조회() {
        //given
        supplementReplyRepository.save(parent1);
        SupplementReply lastParent = supplementReplyRepository.save(parent2);

        //when
        Long lastGroup = supplementReplyRepository.findByParentLastGroup(supplement.getId());

        //then
        assertThat(lastGroup).isEqualTo(lastParent.getGroups());
    }

    @Test
    @DisplayName("부모 GroupOrder 최댓값 조회하기(최초)")
    public void 부모_댓글_groupOrder_최대값_조회_최초() {
        //given

        //when
        Long lastGroup = supplementReplyRepository.findByParentLastGroup(supplement.getId());

        //then
        assertThat(lastGroup).isEqualTo(null);
    }

    @Test
    @DisplayName("대댓글 같은 Group의 GroupOrder 최댓값")
    public void 대댓글_같은_Group_GroupOrder_최댓값(){
        //given
        SupplementReply parent = supplementReplyRepository.save(parent1);
        supplementReplyRepository.save(child1_1);
        SupplementReply lastChild = supplementReplyRepository.save(child1_2);

        //when
        Long lastGroupOrder = supplementReplyRepository.findByChildLastGroupOrder(supplement.getId(), parent.getId());

        //then
        assertThat(lastGroupOrder).isEqualTo(lastChild.getGroupOrder());
    }

    @Test
    @DisplayName("댓글 Member,댓글Id 로 조회")
    public void 댓글_MEMBER_댓글ID로_조회(){
        //given
        SupplementReply saveSupplementReply = supplementReplyRepository.save(parent1);

        //when
        Optional<SupplementReply> findSupplementReply = supplementReplyRepository.findByIdAndMember(saveSupplementReply.getId(), member);

        //then
        isEqualTo(findSupplementReply.get(), supplementReplyRepository.findById(saveSupplementReply.getId()).get());
    }




    @AfterEach
    public void tearDown() {
        supplementRepository.deleteAll();
        supplementReplyRepository.deleteAll();
        memberRepository.deleteAll();
    }

}