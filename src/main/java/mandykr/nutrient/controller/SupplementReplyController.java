package mandykr.nutrient.controller;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.SupplementReplyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
     */
    @GetMapping("{supplementId}")
    public ApiResult<List<SupplementReplyDto>> getSupplementReplyBySupplement(@PathVariable Long supplementId){
        return success(
                supplementReplyService
                        .getSupplementReplyBySupplement(supplementId)
                        .stream()
                        .map(SupplementReplyDto::new)
                        .collect(Collectors.toList())
        );
    }
    
    /**
     * 모든 댓글 보기
     */
    @GetMapping("")
    public ApiResult<List<SupplementReplyDto>> getSupplementReplyList(){
        return success(supplementReplyService
                .getSupplementReplyList()
                .stream()
                .map(SupplementReplyDto::new)
                .collect(Collectors.toList())
        );
    }
    
    /**
     * 처음 댓글 달기
     */
    @PostMapping("{supplementId}")
    public ApiResult<SupplementReplyDto> createSupplementReply(
            @PathVariable("supplementId") Long supplementId
            ,@RequestBody @Valid SupplementReplyRequest request){
        return success(supplementReplyService
                .createSupplementReply(supplementId, getMember(), request)
                .map(SupplementReplyDto::new)
                .get()
        );
    }
    /**
     * 대댓글 달기
     */
    /**
     *
     * @param supplementId
     * @param replyId
     * @param supplementReplyRequest
     * @return
     */
    @PostMapping("{supplementId}/{replyId}")
    public ApiResult<SupplementReplyDto> createSupplementReplyByReply(
            @PathVariable("supplementId") Long supplementId,
            @PathVariable("replyId") Long replyId,
            @RequestBody @Valid SupplementReplyRequest supplementReplyRequest){
        return success(supplementReplyService
                .createSupplementReply(supplementId, replyId, getMember(), supplementReplyRequest)
                .map(SupplementReplyDto::new)
                .get()
        );
    }
    /**
     *
     * @param replyId
     * @param supplementReplyRequest
     * @return
     *         댓글 수정
     */
    @PutMapping("{replyId}")
    public ApiResult<SupplementReplyDto> updSupplementReply(
            @PathVariable Long replyId,
            @Valid SupplementReplyRequest supplementReplyRequest){
        return success(supplementReplyService
                .updateSupplementReply(replyId, getMember(), supplementReplyRequest)
                .map(SupplementReplyDto::new)
                .get()
        );
    }
    /**
     * 대댓글 삭제
     */
    @DeleteMapping("{id}")
    public void deleteSupplementReply(@PathVariable Long supplementId){
        supplementReplyService.deleteSupplementReply(supplementId, getMember());
    }
    //임의의 member 값
    private Member getMember() {
        Member member = memberRepository.findById("testMemberId1").get();
        return member;
    }

}
