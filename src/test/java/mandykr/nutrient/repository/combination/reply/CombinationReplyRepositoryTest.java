package mandykr.nutrient.repository.combination.reply;

import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.repository.combination.CombinationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.standard.expression.Each;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CombinationReplyRepositoryTest {

    @Autowired CombinationReplyRepository replyRepository;
    @Autowired CombinationRepository combinationRepository;

    Combination combination;
    ArrayList<CombinationReply> replyList;
    List<CombinationReply> saveReplyList;

    @BeforeEach
    void setUp() {
        combination = new Combination();
        replyList = new ArrayList<>();

        CombinationReply parent1 = CombinationReply.builder().content("parent1").orders(1L).combination(combination).build();
        replyList.add(parent1);
        replyList.add(CombinationReply.builder().content("child1_1").orders(2L).parent(parent1).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child1_2").orders(2L).parent(parent1).combination(combination).build());

        CombinationReply parent2 = CombinationReply.builder().content("parent2").orders(1L).combination(combination).build();
        replyList.add(parent2);
        replyList.add(CombinationReply.builder().content("child2_1").orders(2L).parent(parent2).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child2_2").orders(2L).parent(parent2).combination(combination).build());

        CombinationReply parent3 = CombinationReply.builder().content("parent3").orders(1L).combination(combination).build();
        replyList.add(parent3);
        replyList.add(CombinationReply.builder().content("child3_1").orders(2L).parent(parent3).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child3_2").orders(2L).parent(parent3).combination(combination).build());

        CombinationReply parent4 = CombinationReply.builder().content("parent4").orders(1L).combination(combination).build();
        replyList.add(parent4);
        replyList.add(CombinationReply.builder().content("child4_1").orders(2L).parent(parent4).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child4_2").orders(2L).parent(parent4).combination(combination).build());

        combinationRepository.save(combination);
        saveReplyList = replyRepository.saveAll(replyList);
    }

    @Test
    void 부모_댓글_조회_페이징() {
        // given
        // when
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        Page<CombinationReply> replyPage =
                replyRepository.findByCombinationAndOrders(
                        combination,
                        CombinationReply.MIN_ORDERS,
                        pageRequest);

        // then
        assertEquals(pageRequest.getPageSize(), replyPage.getSize());
        assertThat(replyPage.getContent().stream().map(CombinationReply::getContent)).contains("parent3", "parent4");
    }

    @Test
    void 자식_댓글_조회_페이징() {
        // given
        CombinationReply combinationReply =
                saveReplyList.stream().filter(r -> "parent1".equals(r.getContent())).collect(Collectors.toList()).get(0);

        // when
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        Page<CombinationReply> replyPage =
                replyRepository.findByParent(
                        combinationReply,
                        pageRequest);

        // then
        assertEquals(pageRequest.getPageSize(), replyPage.getSize());
        assertThat(replyPage.getContent().stream().map(CombinationReply::getContent)).contains("child1_1", "child1_2");
    }

}