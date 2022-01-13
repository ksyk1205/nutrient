package mandykr.nutrient.controller.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.reply.CombinationReplyCreateFormDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyUpdateFormDto;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.service.combination.CombinationReplyService;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/combination-reply")
public class CombinationReplyController {
    private final CombinationReplyService replyService;

    @GetMapping("/{combinationId}")
    public ApiResult<Page<CombinationReplyDto>> getParentReplyByCombination(
            @PathVariable("combinationId") long combinationId,
            final Pageable pageable) {
        return success(replyService
                .getParentReplyByCombination(combinationId, pageable));
    }

    @GetMapping("/{combinationId}/{parentId}")
    public ApiResult<Page<CombinationReplyDto>> getChildrenReplyByParent(
            @PathVariable("combinationId") long combinationId,
            @PathVariable("parentId") long parentId,
            final Pageable pageable) {
        return success(replyService
                .getChildrenReplyByParent(combinationId, parentId, pageable));
    }

    @PostMapping("")
    public ApiResult<Page<CombinationReplyDto>> createReply(@RequestBody @Valid CombinationReplyCreateFormDto dto, final Pageable pageable) {
        CombinationReply resultReply = replyService.createReply(dto);
        return success(getParentOrChildReplyList(resultReply, pageable));
    }

    @PutMapping("/{replyId}")
    public ApiResult<Page<CombinationReplyDto>> updateReply(
            @RequestBody @Valid CombinationReplyUpdateFormDto dto,
            final Pageable pageable) {
        CombinationReply resultReply = replyService.updateReply(dto);
        return success(getParentOrChildReplyList(resultReply, pageable));
    }

    private Page<CombinationReplyDto> getParentOrChildReplyList(CombinationReply reply, Pageable pageable) {
        Page<CombinationReplyDto> resultReplyPage = null;
        if (reply.isParent()) {
            resultReplyPage = replyService.getParentReplyByCombination(reply.getCombination().getId(), pageable);
        }
        else {
            resultReplyPage = replyService.getChildrenReplyByParent(
                    reply.getCombination().getId(), reply.getParent().getId(), pageable);
        }
        return resultReplyPage;
    }

}
