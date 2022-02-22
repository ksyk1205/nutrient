package mandykr.nutrient.controller.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.starRate.SupplementStarRateDto;
import mandykr.nutrient.dto.supplement.starRate.SupplementStarRateRequest;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.repository.member.MemberRepository;
import mandykr.nutrient.service.supplement.SupplementStarRateService;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/star-rate")
public class SupplementStarRateController {
    private final SupplementStarRateService starRateService;
    private final MemberRepository memberRepository;

    /**
     * 별점 등록
     * @param supplementId 영양제 아이디
     * @param starRateRequest 화면에서 입력받는 값(별점 갯수)
     * @return
     */
    @PostMapping("/{supplementId}")
    public ApiResult<SupplementStarRateDto> createStarRate(
            @PathVariable Long supplementId,
            @RequestBody @Valid SupplementStarRateRequest starRateRequest){
        return success(starRateService.createStarRate(supplementId, starRateRequest.getStarNumber(), getMember()));
    }

    /**
     * 별점 수정
     * @param supplementId 영양제 아이디
     * @param starRateId 별점 아이디
     * @param starRateRequest 화면에서 입력받는 값(별점 갯수)
     * @return
     */
    @PutMapping("/{supplementId}/{starRateId}")
    public ApiResult<SupplementStarRateDto> updateStarRate(
            @PathVariable Long supplementId,
            @PathVariable Long starRateId,
            @RequestBody @Valid SupplementStarRateRequest starRateRequest){
        return success(starRateService.updateStarRate(supplementId, starRateId, starRateRequest.getStarNumber(), getMember()));
    }

    /**
     * 별점 조회(회원 정보와, 영양제번호로)
     * @param supplementId 영양제아이디
     * @return
     */
    @GetMapping("/{supplementId}")
    public ApiResult<SupplementStarRateDto> getStarRateWithMember(
            @PathVariable Long supplementId
            //, Member member
            ){

        return success(starRateService.getStarRateWithMember(supplementId, getMember()));
    }

    //임의의 member 값
    private Member getMember() {
        return memberRepository.findById("testMemberId1").orElseThrow(() -> new IllegalArgumentException("Member가 존재하지 않습니다."));
    }

    /**
     * 별점 삭제
     * @param starRateId 별점 아이디
     */
    @DeleteMapping("/{starRateId}")
    public void deleteStarRate(@PathVariable Long starRateId){
                                //, Member meber
        starRateService.deleteStarRate(starRateId, getMember());
    }

}
