package mandykr.nutrient.controller;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.service.SupplementReplyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static mandykr.nutrient.util.ApiUtils.ApiResult;
import static mandykr.nutrient.util.ApiUtils.success;

@Controller
@RequiredArgsConstructor
public class SupplementReplyController {
    private final SupplementReplyService supplementReplyService;

    @GetMapping("/supplement-reply/{id}") //영양제 ID 조회
    public ApiResult<List<SupplementReplyDto>> getSupplementReplyBySupplement(@PathVariable Long id){
        return success(
                supplementReplyService
                        .getSupplementReplyBySupplement(id)
                        .stream()
                        .map(SupplementReplyDto::new)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/supplement-reply")
    public ApiResult<List<SupplementReplyDto>> getSupplementReplyList(){
        return success(supplementReplyService
                .getSupplementReplyList()
                .stream()
                .map(SupplementReplyDto::new)
                .collect(Collectors.toList())
        );
    }

    @PutMapping("/supplement-reply/{id}")
    public ApiResult<SupplementReplyDto> createSupplementReply(
            @PathVariable Long supplementId
            , SupplementReplyDto supplementReplyDto){
        return success(supplementReplyService
                .createSupplementReply(supplementId,supplementReplyDto)
                .map(SupplementReplyDto::new)
                .get()
        );
    }

    @DeleteMapping("/supplement-reply/{id}")
    public void deleteSupplementReply(
            @PathVariable Long supplementId){
        supplementReplyService.deleteSupplementReply(supplementId);
    }

    @PostMapping("/supplement-reply/{id}")
    public ApiResult<SupplementReplyDto> updSupplementReply(
            @PathVariable Long supplementId
            , SupplementReplyDto supplementReplyDto){
        return success(supplementReplyService
                .updateSupplementReply(supplementId,supplementReplyDto)
                .map(SupplementReplyDto::new)
                .get()
        );
    }
}
