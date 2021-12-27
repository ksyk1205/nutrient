package mandykr.nutrient.controller;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.dto.request.StarRateRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.StarRateService;
import mandykr.nutrient.util.ApiUtils;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/star-rate")
public class StarRateController {
    private final StarRateService starRateService;
    private final MemberRepository memberRepository;

    /**
     * 별점 등록
     * @param supplementId 영양제 아이디
     * @param starRateRequest 화면에서 입력받는 값(별점 갯수)
     * @return
     */
    @PostMapping("/{supplementId}")
    public ApiResult<StarRateDto> createStarRate(
            @PathVariable Long supplementId
            , @RequestBody @Valid StarRateRequest starRateRequest){
        return success(starRateService.createStarRate(supplementId, starRateRequest.getStarNumber(),getMember()));
    }

    /**
     * 별점 수정
     * @param supplementId 영양제 아이디
     * @param starRateId 별점 아이디
     * @param starRateRequest 화면에서 입력받는 값(별점 갯수)
     * @return
     */
    @PutMapping("/{supplementId}/{starRateId}")
    public ApiResult<StarRateDto> updateStarRate(
            @PathVariable Long supplementId
            , @PathVariable Long starRateId
            , @RequestBody @Valid StarRateRequest starRateRequest){
        return success(starRateService.updateStarRate(supplementId,starRateId,starRateRequest.getStarNumber(),getMember()));
    }

    /**
     * 별점 조회(회원 정보와, 영양제번호로)
     * @param supplementId 영양제아이디
     * @return
     */
    @GetMapping("/{supplementId}")
    public ApiResult<StarRateDto> getMemberStarRate(
            @PathVariable Long supplementId
            //, Member member
            ){

        return success(starRateService.getMemberStarRate(supplementId,getMember()));
    }

    //임의의 member 값
    private Member getMember() {
        Member member = memberRepository.findById("testMemberId1").get();
        return member;
    }

}
