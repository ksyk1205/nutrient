package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyUpdateFormDto;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        replyList.add(CombinationReply.builder().content("parent1").orders(1).combination(combination).build());
        replyList.add(CombinationReply.builder().content("parent2").orders(1).combination(combination).build());
        replyList.add(CombinationReply.builder().content("parent3").orders(1).combination(combination).build());

        Pageable pageRequest = PageRequest.of(0, 3);
        Page<CombinationReply> replyPageResult = new PageImpl<>(replyList);

        given(replyRepository.findByCombinationAndOrders(
                any(Combination.class), anyInt(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getParentReplyByCombination(combination.getId(), pageRequest);

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
        CombinationReply parent = CombinationReply.builder().content("parent").orders(1).combination(combination).build();
        replyList.add(parent);
        replyList.add(CombinationReply.builder().content("child1_1").orders(2).parent(parent).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child1_2").orders(2).parent(parent).combination(combination).build());
        replyList.add(CombinationReply.builder().content("child1_3").orders(2).parent(parent).combination(combination).build());

        Pageable pageRequest = PageRequest.of(0, 3);
        Page<CombinationReply> replyPageResult = new PageImpl<>(replyList);

        given(replyRepository.findByCombinationAndOrders(
                any(Combination.class), anyInt(), any(PageRequest.class)))
                .willReturn(replyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getParentReplyByCombination(combination.getId(), pageRequest);

        // then
        assertThat(
                findReplyPage.map(CombinationReplyDto::getContent))
                .containsAll(replyList.stream().map(CombinationReply::getContent)
                        .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("변경된 댓글 내용을 전달하면 수정된 댓글 Entity를 반환한다.")
    void updateReply() {
        // given
        CombinationReplyUpdateFormDto updateReplyDto = CombinationReplyUpdateFormDto.builder().id(1L).content("update content").build();
        CombinationReply findReply = CombinationReply.builder().content("parent").orders(1).build();

        given(replyRepository.findById(updateReplyDto.getId())).willReturn(Optional.of(findReply));

        // when
        CombinationReply updateReply = replyService.updateReply(updateReplyDto);

        // then
        assertEquals(updateReplyDto.getContent(), updateReply.getContent());
    }

}