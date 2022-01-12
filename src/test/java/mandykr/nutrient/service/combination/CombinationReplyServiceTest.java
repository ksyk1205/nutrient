package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.repository.combination.reply.CombinationReplyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class CombinationReplyServiceTest {
    CombinationReplyRepository replyRepository = mock(CombinationReplyRepository.class);
    CombinationReplyService replyService = new CombinationReplyService(replyRepository);

    @Test
    @DisplayName("부모 댓글 리스트를 페이징으로 조회하면 댓글 Entity를 DTO로 변환해 리턴한다.")
    void getCombinationReplyByCombination() {
        // given
        Combination combination = new Combination(1L);
        ArrayList<CombinationReply> replyList = new ArrayList<>();
        replyList.add(CombinationReply.builder().content("parent1").orders(1L).combination(combination).build());
        replyList.add(CombinationReply.builder().content("parent2").orders(1L).combination(combination).build());
        replyList.add(CombinationReply.builder().content("parent3").orders(1L).combination(combination).build());

        Pageable pageRequest = PageRequest.of(0, 3);
        Page<CombinationReply> replyPageResult = new PageImpl<>(replyList);

        given(replyRepository.findByCombinationAndOrders(
                any(Combination.class), anyInt(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getCombinationReplyByCombination(combination.getId(), pageRequest);

        // then
        assertThat(
                findReplyPage.map(CombinationReplyDto::getContent))
                .containsAll(replyList.stream().map(CombinationReply::getContent)
                        .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("자식 댓글 리스트를 페이징으로 조회하면 댓글 Entity를 DTO로 변환해 리턴한다.")
    void getCombinationReplyByParent() {
        // given
        Combination combination = new Combination(1L);
        ArrayList<CombinationReply> replyList = new ArrayList<>();
        CombinationReply parent1 = CombinationReply.builder().content("parent1").orders(1L).combination(combination).build();
        replyList.add(parent1);
        replyList.add(CombinationReply.builder().content("child1_1").orders(2L).parent(parent1).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child1_2").orders(2L).parent(parent1).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child1_3").orders(2L).parent(parent1).combination(combination).build());

        Pageable pageRequest = PageRequest.of(0, 3);
        Page<CombinationReply> replyPageResult = new PageImpl<>(replyList);

        given(replyRepository.findByCombinationAndOrders(
                any(Combination.class), anyInt(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getCombinationReplyByCombination(combination.getId(), pageRequest);

        // then
        assertThat(
                findReplyPage.map(CombinationReplyDto::getContent))
                .containsAll(replyList.stream().map(CombinationReply::getContent)
                        .collect(Collectors.toList()));
    }
}