package mandykr.nutrient.controller.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.reply.SupplementReplyDto;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.supplement.SupplementReplyService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static mandykr.nutrient.util.ApiUtils.ApiResult;
import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplement-reply")
public class SupplementReplyController {
    private final SupplementReplyService supplementReplyService;
    private final MemberRepository memberRepository;

    /**
     * 해당 영양제ID 의 모든 댓글 보기
     * Depth가 1인거만 보기
     * @param supplementId
     * @return
     */
    @GetMapping("{supplementId}")
    public ApiResult<List<SupplementReplyDto>> getSupplementReplyBySupplement(@PathVariable Long supplementId){
        return success(supplementReplyService.getSupplementReplyBySupplement(supplementId));
    }

    /**
     * 해당 영양제 ID 댓글의 대댓글 보기
     * Depth가 2인거만 보기
     * @return
     */
    @GetMapping("{supplementId}/{parentId}")
    public ApiResult<List<SupplementReplyDto>> getSupplementReplyList(
            @PathVariable Long supplementId,
            @PathVariable Long parentId ){
        return success(supplementReplyService.getSupplementReplyBySupplementWithParent(supplementId, parentId));
    }

    /**
     * 댓글 달기
     * @param supplementId
     * @param request
     * @return
     */
    @PostMapping("{supplementId}")
    public ApiResult<SupplementReplyDto> createSupplementReply(
            @PathVariable("supplementId") Long supplementId
            ,@RequestBody @Valid SupplementReplyRequest request){
        return success(supplementReplyService.createSupplementReply(supplementId, getMember(), SupplementReplyDto.toSupplementReplyDto(request.getContent())));
    }


    /**
     * 대댓글 달기
     * @param supplementId
     * @param replyId
     * @param request
     * @return
     */
    @PostMapping("{supplementId}/{replyId}")
    public ApiResult<SupplementReplyDto> createSupplementReplyByReply(
            @PathVariable("supplementId") Long supplementId,
            @PathVariable("replyId") Long replyId,
            @RequestBody @Valid SupplementReplyRequest request){
        return success(supplementReplyService.createSupplementReply(supplementId, replyId, getMember(), SupplementReplyDto.toSupplementReplyDto(request.getContent())));
    }


    /**
     * 댓글 수정
     * @param replyId
     * @param request
     * @return
     */
    @PutMapping("{replyId}")
    public ApiResult<SupplementReplyDto> updateSupplementReply(
            @PathVariable Long replyId,
            @RequestBody @Valid SupplementReplyRequest request){
        return success(supplementReplyService.updateSupplementReply(replyId, getMember(), SupplementReplyDto.toSupplementReplyDto(request.getContent())));
    }

    /**
     * 댓글, 대댓글 삭제
     * @param replyId
     */
    @DeleteMapping("{replyId}")
    public ApiResult<Boolean> deleteSupplementReply(@PathVariable Long replyId){
        supplementReplyService.deleteSupplementReply(replyId, getMember());
        return success(true);
    }

    
    //임의의 member 값
    private Member getMember() {
        Member member = memberRepository.findById("testMemberId1").get();
        return member;
    }

}
