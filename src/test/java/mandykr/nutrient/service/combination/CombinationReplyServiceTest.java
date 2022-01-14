package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyUpdateFormDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.repository.combination.reply.CombinationReplyRepository;
import mandykr.nutrient.util.PageRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;

class CombinationReplyServiceTest {
    CombinationReplyRepository replyRepository = mock(CombinationReplyRepository.class);
    CombinationReplyService replyService = new CombinationReplyService(replyRepository);

    Combination combination;
    ArrayList<CombinationReply> parentReplyList;
    CombinationReply parent;
    ArrayList<CombinationReply> childReplyList;

    Pageable pageRequest;
    int page = 1;
    int size = 2;

    Page<CombinationReply> parentReplyPageResult;
    Page<CombinationReply> childReplyPageResult;

    @BeforeEach
    void setup() {
        combination = new Combination(1L);

        parentReplyList = new ArrayList<>();
        parent = CombinationReply.builder().id(1L).content("parent1").orders(1L).combination(combination).build();
        parentReplyList.add(parent);
        parentReplyList.add(CombinationReply.builder().id(2L).content("parent2").orders(1L).combination(combination).build());
        parentReplyList.add(CombinationReply.builder().id(3L).content("parent3").orders(1L).combination(combination).build());

        childReplyList = new ArrayList<>();
        childReplyList.add(CombinationReply.builder().id(11L).content("child1_1").orders(2L).parent(parent).combination(combination).build());
        childReplyList.add(CombinationReply.builder().id(12L).content("child1_2").orders(2L).parent(parent).combination(combination).build());
        childReplyList.add(CombinationReply.builder().id(13L).content("child1_3").orders(2L).parent(parent).combination(combination).build());

        pageRequest = new PageRequestUtil(page, size).getPageable();
        parentReplyPageResult = new PageImpl<>(parentReplyList, pageRequest, parentReplyList.size());
        childReplyPageResult = new PageImpl<>(childReplyList, pageRequest, childReplyList.size());
    }

    @Test
    @DisplayName("부모 댓글 리스트를 페이징으로 조회하면 댓글 Entity를 DTO로 변환해 리턴한다.")
    void getCombinationReplyByCombination() {
        // given
        given(replyRepository.findByCombinationAndOrders(
                any(Combination.class), anyInt(), any(PageRequest.class)))
                .willReturn(parentReplyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getParentReplyByCombination(combination.getId(), pageRequest);

        // then
        assertThat(
                findReplyPage.map(CombinationReplyDto::getContent))
                .containsAll(parentReplyList.stream().map(CombinationReply::getContent)
                        .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("자식 댓글 리스트를 페이징으로 조회하면 댓글 Entity를 DTO로 변환해 리턴한다.")
    void getCombinationReplyByParent() {
        // given
        given(replyRepository.findByCombinationAndParent(
                any(Combination.class), any(CombinationReply.class), any(PageRequest.class)))
                .willReturn(childReplyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getChildrenReplyByParent(
                combination.getId(), parent.getId(), pageRequest);

        // then
        assertThat(
                findReplyPage.map(CombinationReplyDto::getContent))
                .containsAll(childReplyList.stream().map(CombinationReply::getContent)
                        .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("변경된 댓글 내용을 전달하면 수정된 댓글 Entity를 반환한다.")
    void updateReply() {
        // given
        CombinationReplyUpdateFormDto beforeReplyDto = CombinationReplyUpdateFormDto.builder().id(1L).content("update content").build();
        CombinationReply findReply = CombinationReply.builder().content("parent").orders(1).combination(combination).build();

        given(replyRepository.findById(beforeReplyDto.getId())).willReturn(Optional.of(findReply));

        // when
        CombinationReplyDto afterReplyDto = replyService.updateReply(beforeReplyDto);

        // then
        assertEquals(beforeReplyDto.getContent(), afterReplyDto.getContent());
    }

    @Test
    @DisplayName("전달되는 댓글이 부모면 부모 리스트를 반환한다.")
    void getParentReplyList() {
        // given
        CombinationReplyDto parentDto = CombinationReplyDto.builder()
                .content("parentReply")
                .orders(CombinationReply.PARENT_ORDERS)
                .combinationId(combination.getId())
                .build();

        given(replyRepository.findByCombinationAndOrders(
                any(Combination.class), anyInt(), any(PageRequest.class)))
                .willReturn(parentReplyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getParentOrChildReplyList(parentDto, pageRequest);

        // then
        assertThat(
                findReplyPage.map(CombinationReplyDto::getContent))
                .containsAll(parentReplyList.stream().map(CombinationReply::getContent)
                        .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("전달되는 댓글이 자식이면 자식 리스트를 반환한다.")
    void getChildReplyList() {
        // given
        CombinationReplyDto childDto = CombinationReplyDto.builder()
                .content("childReply")
                .orders(CombinationReply.CHILD_ORDERS)
                .combinationId(combination.getId())
                .parentId(1L)
                .build();

        given(replyRepository.findByCombinationAndParent(
                any(Combination.class), any(CombinationReply.class), any(PageRequest.class)))
                .willReturn(childReplyPageResult);

        // when
        Page<CombinationReplyDto> findReplyPage = replyService.getParentOrChildReplyList(childDto, pageRequest);

        // then
        assertThat(
                findReplyPage.map(CombinationReplyDto::getContent))
                .containsAll(childReplyList.stream().map(CombinationReply::getContent)
                        .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("논리적, 물리적 삭제가 되지 않은 자식 댓글이 있으면 논리적으로 삭제한다.")
    void deleteReplyLogical() {
        // given
        childReplyList.forEach(CombinationReply::deleteLogical);

        given(replyRepository.findById(parent.getId()))
                .willReturn(Optional.of(parent));

        // when
        replyService.deleteReply(parent.getId());

        // then
        then(replyRepository).should(times(1))
                .deleteById(parent.getId());
        then(replyRepository).should(times(1))
                .deleteAllByIdInBatch(any(List.class));
    }

    @Test
    @DisplayName("논리적, 물리적 삭제가 되지 않은 자식 댓글이 없으면 물리적으로 삭제한다.")
    void deleteReplyPhysical() {
        // given
        childReplyList.forEach(c -> parent.addChild(c));
        given(replyRepository.findById(parent.getId()))
                .willReturn(Optional.of(parent));

        // when
        replyService.deleteReply(parent.getId());

        // then
        assertTrue(parent.isDeleted());
    }

}