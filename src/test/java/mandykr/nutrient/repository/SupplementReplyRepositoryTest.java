package mandykr.nutrient.repository;

import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.by;

@DataJpaTest //JPA 테스트
class SupplementReplyRepositoryTest {
    @Autowired
    SupplementReplyRepository supplementReplyRepository;
    @Autowired
    SupplementRepository supplementRepository;

    @Autowired
    MemberRepository memberRepository;

    //미리 주입할 데이터
    SupplementReply parent1;
    SupplementReply child11;
    SupplementReply child12;
    SupplementReply parent2;
    SupplementReply child21;
    Supplement saveSupplement1;
    Member member;
    @BeforeEach
    public void setup(){
        Supplement supplement1 = new Supplement();
        supplement1.setName("test1");
        this.saveSupplement1 = supplementRepository.save(supplement1);

        Member member = new Member();
        member.setMemberId("testMemberId1");
        member.setName("martin");
        this.member =  memberRepository.save(member);

        parent1 = SupplementReply
                .builder()
                .content("reply1")
                .groups(1L)
                .groupOrder(1L)
                .member(member)
                .deleteFlag(false)
                .parent(null)
                .supplement(supplement1)
                .build();
        child11 = SupplementReply
                .builder()
                .content("reply1-1")
                .groups(1L)
                .groupOrder(2L)
                .member(member)
                .deleteFlag(false)
                .supplement(supplement1)
                .build();
        child11.addParents(parent1);
        child12 = SupplementReply
                .builder()
                .content("reply1-2")
                .groups(1L)
                .groupOrder(3L)
                .member(member)
                .deleteFlag(false)
                .supplement(supplement1)
                .build();
        child12.addParents(parent1);
        parent2 = SupplementReply
                .builder()
                .content("reply2")
                .groups(2L)
                .groupOrder(1L)
                .member(member)
                .deleteFlag(false)
                .parent(null)
                .supplement(supplement1)
                .build();
        child21 = SupplementReply
                .builder()
                .content("reply2-1")
                .groups(2L)
                .groupOrder(2L)
                .member(member)
                .deleteFlag(false)
                .supplement(supplement1)
                .build();
        child21.addParents(parent2);
    }

    @Test
    public void 댓글_등록() throws Exception{

        //given
        //when
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);
        child11.addParents(saveParent1);
        SupplementReply saveChild11 = supplementReplyRepository.save(child11);
        child12.addParents(saveParent1);
        SupplementReply saveChild12 = supplementReplyRepository.save(child12);
        SupplementReply saveParent2 = supplementReplyRepository.save(parent2);
        child21.addParents(saveParent2);
        SupplementReply saveChild21 = supplementReplyRepository.save(child21);


        //then
        assertThat(saveParent1).isEqualTo(supplementReplyRepository.findById(saveParent1.getId()).get());
        assertThat(saveChild11).isEqualTo(supplementReplyRepository.findById(saveChild11.getId()).get());
        assertThat(saveChild12).isEqualTo(supplementReplyRepository.findById(saveChild12.getId()).get());
        assertThat(saveParent2).isEqualTo(supplementReplyRepository.findById(saveParent2.getId()).get());
        assertThat(saveChild21).isEqualTo(supplementReplyRepository.findById(saveChild21.getId()).get());
    }


    @Test
    public void 댓글_수정() throws Exception{
        //given
        //when
        SupplementReply saveParent = supplementReplyRepository.save(parent1);
        saveParent.changeContent("UPDATE");

        //then
        assertThat(supplementReplyRepository.findById(saveParent.getId()).get().getContent()).isEqualTo("UPDATE");
    }

    @Test
    public void 댓글_영양제로_조회() throws Exception{
        //given
        //when
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);
        child11.addParents(saveParent1);
        SupplementReply saveChild11 = supplementReplyRepository.save(child11);
        child12.addParents(saveParent1);
        SupplementReply saveChild12 = supplementReplyRepository.save(child12);
        SupplementReply saveParent2 = supplementReplyRepository.save(parent2);
        child21.addParents(saveParent2);
        SupplementReply saveChild21 = supplementReplyRepository.save(child21);

        List<SupplementReply> supplementReplies1 = supplementReplyRepository.findBySupplement(saveSupplement1, by(ASC, "groups", "groupOrder"));

        //then
        assertThat(supplementReplies1.size()).isEqualTo(5);
        assertThat(supplementReplies1.get(0).getContent()).isEqualTo("reply1");
        assertThat(supplementReplies1.get(1).getContent()).isEqualTo("reply1-1");
        assertThat(supplementReplies1.get(2).getContent()).isEqualTo("reply1-2");
        assertThat(supplementReplies1.get(3).getContent()).isEqualTo("reply2");
        assertThat(supplementReplies1.get(4).getContent()).isEqualTo("reply2-1");

    }

    @Test
    public void 댓글_ORDER_최대값_조회() throws Exception{
        //given
        //when
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);
        child11.addParents(saveParent1);
        SupplementReply saveChild11 = supplementReplyRepository.save(child11);
        child12.addParents(saveParent1);
        SupplementReply saveChild12 = supplementReplyRepository.save(child12);
        SupplementReply saveParent2 = supplementReplyRepository.save(parent2);
        child21.addParents(saveParent2);
        SupplementReply saveChild21 = supplementReplyRepository.save(child21);

        assertThat(supplementReplyRepository.findByLastOrderWithParent(saveSupplement1.getId())).isEqualTo(2L);
    }

    @Test
    public void 대댓글_ORDER_최대값_조회() throws Exception{
        //given
        //when
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);
        child11.addParents(saveParent1);
        SupplementReply saveChild11 = supplementReplyRepository.save(child11);
        child12.addParents(saveParent1);
        SupplementReply saveChild12 = supplementReplyRepository.save(child12);

        assertThat(supplementReplyRepository.findByLastOrderWithChild(saveSupplement1.getId(),saveParent1.getId())).isEqualTo(3L);
    }

    @Test
    public void 댓글_ID_MEMBER_조회() throws Exception{
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);
        SupplementReply supplementReply = supplementReplyRepository.findByIdAndMember(saveParent1.getId(), member).get();
        assertThat(supplementReply.getContent()).isEqualTo("reply1");
    }
    
    @Test
    public void 댓글_정렬_조회() throws Exception{
        //given
        Supplement supplement2 = new Supplement();
        supplement2.setName("test2");
        Supplement saveSupplement2 = supplementRepository.save(supplement2);

        //when
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);
        child11.addParents(saveParent1);
        SupplementReply saveChild11 = supplementReplyRepository.save(child11);
        child12.addParents(saveParent1);
        SupplementReply saveChild12 = supplementReplyRepository.save(child12);
        parent2.builder().supplement(saveSupplement2);
        SupplementReply saveParent2 = supplementReplyRepository.save(parent2);
        child21.builder().supplement(saveSupplement2);
        child21.addParents(saveParent2);
        SupplementReply saveChild21 = supplementReplyRepository.save(child21);

        //then
        List<SupplementReply> supplementReplies = supplementReplyRepository.findAll(by(ASC, "groups", "groupOrder"));
        assertThat(supplementReplies.get(0).getContent()).isEqualTo("reply1");
        assertThat(supplementReplies.get(1).getContent()).isEqualTo("reply1-1");
        assertThat(supplementReplies.get(2).getContent()).isEqualTo("reply1-2");
        assertThat(supplementReplies.get(3).getContent()).isEqualTo("reply2");
        assertThat(supplementReplies.get(4).getContent()).isEqualTo("reply2-1");

    }

    @AfterEach
    public void tearDown() {
        supplementRepository.deleteAll();
        supplementReplyRepository.deleteAll();
        memberRepository.deleteAll();
    }
}