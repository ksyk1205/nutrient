package mandykr.nutrient.controller.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.reply.SupplementReplyRequestDto;
import mandykr.nutrient.dto.supplement.reply.SupplementReplyResponseDto;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.supplement.SupplementReplyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
     * 해당 영양제ID 의 모든 댓글 보기(Depth가 1인거만 보기)
     * @param  supplementId
     * @return ApiResult<List<SupplementReplyResponseDto>>
     */
    @GetMapping("{supplementId}")
    public ApiResult<Page<SupplementReplyResponseDto>> getSupplementRepliesWithParent(
            @PathVariable Long supplementId,
            final @PageableDefault(sort = "groupOrder") Pageable pageable){
        return success(supplementReplyService.getSupplementRepliesWithParent(supplementId, pageable));
    }

    /**
     * 해당 영양제 ID 댓글의 대댓글 보기
     * Depth가 2인거만 보기
     * @return ApiResult<List<SupplementReplyResponseDto>>
     */
    @GetMapping("{supplementId}/{parentId}")
    public ApiResult<Page<SupplementReplyResponseDto>> getSupplementRepliesWithChild(
            @PathVariable Long supplementId,
            @PathVariable Long parentId,
            final @PageableDefault(sort = "groups") Pageable pageable){
        return success(supplementReplyService.getSupplementRepliesWithChild(supplementId, parentId, pageable));
    }

    /**
     * 댓글 달기
     * @param supplementId
     * @param request
     * @return ApiResult<SupplementReplyResponseDto>
     */
    @PostMapping("{supplementId}")
    public ApiResult<SupplementReplyResponseDto> createSupplementReply(
            @PathVariable("supplementId") Long supplementId,
            @RequestBody @Valid SupplementReplyRequest request){
        SupplementReplyRequestDto supplementReplyRequestDto = new SupplementReplyRequestDto(request);
        return success(supplementReplyService.createSupplementReply(supplementId, getMember(), supplementReplyRequestDto));
    }


    /**
     * 대댓글 달기
     * @param supplementId
     * @param replyId
     * @param request
     * @return ApiResult<SupplementReplyResponseDto>
     */
    @PostMapping("{supplementId}/{replyId}")
    public ApiResult<SupplementReplyResponseDto> createSupplementReplyByReply(
            @PathVariable("supplementId") Long supplementId,
            @PathVariable("replyId") Long replyId,
            @RequestBody @Valid SupplementReplyRequest request){
        SupplementReplyRequestDto supplementReplyRequestDto = new SupplementReplyRequestDto(request);
        return success(supplementReplyService.createSupplementReply(supplementId, replyId, getMember(), supplementReplyRequestDto));
    }


    /**
     * 댓글 수정
     * @param replyId
     * @param request
     * @return ApiResult<SupplementReplyResponseDto>
     */
    @PutMapping("{replyId}")
    public ApiResult<SupplementReplyResponseDto> updateSupplementReply(
            @PathVariable Long replyId,
            @RequestBody @Valid SupplementReplyRequest request){
        SupplementReplyRequestDto supplementReplyRequestDto = new SupplementReplyRequestDto(request);
        return success(supplementReplyService.updateSupplementReply(replyId, getMember(), supplementReplyRequestDto));
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
        return memberRepository.findById("testMemberId1").orElseThrow(() -> new IllegalArgumentException("Member가 존재하지 않습니다."));
    }

}
