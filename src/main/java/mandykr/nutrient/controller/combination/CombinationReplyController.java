package mandykr.nutrient.controller.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.service.combination.CombinationReplyService;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/combination-reply")
public class CombinationReplyController {
    private final CombinationReplyService replyService;

    @GetMapping("/{combinationId}")
    public ApiResult<Page<CombinationReplyDto>> getCombinationReplyByCombination(
            @PathVariable("combinationId") long combinationId,
            @RequestBody Pageable pageable) {
        return success(replyService
                .getCombinationReplyByCombination(combinationId, pageable));
    }
}
