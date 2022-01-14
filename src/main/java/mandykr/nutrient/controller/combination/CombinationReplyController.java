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

    @GetMapping("/combination-reply/{combinationId}")
    public ApiResult<Page<CombinationReplyDto>> getParentReplyByCombination(
            @PathVariable("combinationId") long combinationId,
            final Pageable pageable) {
        return success(replyService
                .getParentReplyByCombination(combinationId, pageable));
    }

    @GetMapping("/combination-reply/{combinationId}/{parentId}")
    public ApiResult<Page<CombinationReplyDto>> getChildrenReplyByParent(
            @PathVariable("combinationId") long combinationId,
            @PathVariable("parentId") long parentId,
            final Pageable pageable) {
        return success(replyService
                .getChildrenReplyByParent(combinationId, parentId, pageable));
    }

    @PostMapping("/combination-reply")
    public ApiResult<Page<CombinationReplyDto>> createReply(@RequestBody @Valid CombinationReplyCreateFormDto dto, final Pageable pageable) {
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
