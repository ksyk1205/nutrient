package mandykr.nutrient.controller.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.reply.CombinationReplyCreateFormDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyUpdateFormDto;
import mandykr.nutrient.service.combination.CombinationReplyService;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CombinationReplyController {

    private final CombinationReplyService replyService;

    /**
     * 영양제 조합 정보를 입력받아 부모 댓글 목록을 페이징으로 반환한다.
     */
    @GetMapping("/combination-reply/{combinationId}")
    public ApiResult<Page<CombinationReplyDto>> getParentRepliesByCombination(
        @PathVariable("combinationId") long combinationId,
        final Pageable pageable) {
        return success(replyService
            .getParentsReplyByCombination(combinationId, pageable));
    }

    /**
     * 영양제 조합과 부모 댓글 정보를를 입력받아 자식 댓글 목록을 페이징으로 반환한다.
     */
    @GetMapping("/combination-reply/{combinationId}/{parentId}")
    public ApiResult<Page<CombinationReplyDto>> getChildRepliesByParent(
        @PathVariable("combinationId") long combinationId,
        @PathVariable("parentId") long parentId,
        final Pageable pageable) {
        return success(replyService
            .getChildrenReplyByParent(combinationId, parentId, pageable));
    }

    @PostMapping("/combination-reply")
    public ApiResult<Page<CombinationReplyDto>> createReply(
        @RequestBody @Valid CombinationReplyCreateFormDto dto, final Pageable pageable) {
        CombinationReplyDto resultReplyDto = replyService.createReply(dto);
        return success(replyService.getParentOrChildReplyList(resultReplyDto, pageable));
    }

    @PutMapping("/combination-reply/{replyId}")
    public ApiResult<Page<CombinationReplyDto>> updateReply(
        @RequestBody @Valid CombinationReplyUpdateFormDto dto,
        final Pageable pageable) {
        CombinationReplyDto resultReplyDto = replyService.updateReply(dto);
        return success(replyService.getParentOrChildReplyList(resultReplyDto, pageable));
    }

    @DeleteMapping("/combination-reply/{replyId}")
    public ApiResult<Page<CombinationReplyDto>> deleteReply(
        @PathVariable("replyId") Long replyId,
        final Pageable pageable) {
        CombinationReplyDto replyDto = replyService.getReplyDto(replyId);
        replyService.deleteReply(replyId);
        return success(replyService.getParentOrChildReplyList(replyDto, pageable));
    }
}
