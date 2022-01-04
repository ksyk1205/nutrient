package mandykr.nutrient.repository;

import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest //JPA 테스트
class SupplementReplyRepositoryTest {
    @Autowired
    SupplementReplyRepository supplementReplyRepository;
    @Autowired
    SupplementRepository supplementRepository;

    //미리 주입할 데이터
    SupplementReply parent1;
    SupplementReply child11;
    SupplementReply child12;
    SupplementReply parent2;
    SupplementReply child21;
    Supplement saveSupplement1;
    Supplement saveSupplement2;
    @BeforeEach
    public void setup(){
        Supplement supplement1 = new Supplement();
        supplement1.setName("test1");
        Supplement supplement2 = new Supplement();
        supplement2.setName("test2");
        saveSupplement1 = supplementRepository.save(supplement1);
        saveSupplement2 = supplementRepository.save(supplement2);
        parent1 = SupplementReply
                .builder()
                .content("reply1")
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(supplement1)
                .build();
        child11 = SupplementReply
                .builder()
                .content("reply1-1")
                .orders(2)
                .deleteFlag(false)
                .supplement(supplement1)
                .build();
        child11.addParents(parent1);
        child12 = SupplementReply
                .builder()
                .content("reply1-2")
                .orders(2)
                .deleteFlag(false)
                .supplement(supplement1)
                .build();
        child12.addParents(parent1);
        parent2 = SupplementReply
                .builder()
                .content("reply2")
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(supplement2)
                .build();
        child21 = SupplementReply
                .builder()
                .content("reply2-1")
                .orders(2)
                .deleteFlag(false)
                .supplement(supplement2)
                .build();
        child21.addParents(parent2);
    }

    @Test
    public void 댓글_등록() throws Exception{

        //given
        //when
        SupplementReply saveParent1 = supplementReplyRepository.save(parent1);
        SupplementReply saveChild11 = supplementReplyRepository.save(child11);
        SupplementReply saveChild12 = supplementReplyRepository.save(child12);
        SupplementReply saveParent2 = supplementReplyRepository.save(parent2);
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
        SupplementReply saveChild11 = supplementReplyRepository.save(child11);
        SupplementReply saveChild12 = supplementReplyRepository.save(child12);
        SupplementReply saveParent2 = supplementReplyRepository.save(parent2);
        SupplementReply saveChild21 = supplementReplyRepository.save(child21);

        //then
        assertThat(supplementReplyRepository.findBySupplement(saveSupplement1).size()).isEqualTo(3);
        assertThat(supplementReplyRepository.findBySupplement(saveSupplement2).size()).isEqualTo(2);

    }

    @AfterEach
    public void tearDown() {
        supplementRepository.deleteAll();
        supplementReplyRepository.deleteAll();
    }
}