package mandykr.nutrient.controller.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.starRate.CombinationStarRateDto;
import mandykr.nutrient.dto.combination.starRate.request.CombinationStarRateRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.combination.CombinationStarRateService;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequestMapping("/api/combination-star-rate")
@RequiredArgsConstructor
public class CombinationStarRateController {

    private final CombinationStarRateService combinationStarRateService;

    private final MemberRepository memberRepository;


    @GetMapping("{combinationId}")
    public ApiResult<CombinationStarRateDto> getCombinationStarRateByCombination(
        @PathVariable Long combinationId) {
        return success(
            combinationStarRateService
                .getCombinationStarRateByCombination(combinationId, getMember())
        );
    }

    @PostMapping("{combinationId}")
    public ApiResult<CombinationStarRateDto> createCombinationStarRate(
        @PathVariable Long combinationId,
        @Valid @RequestBody CombinationStarRateRequest request) {
        return success(
            combinationStarRateService
                .createCombinationStarRate(combinationId, getMember(), request));
    }

    @PutMapping("{combinationId}/{combinationStarRateId}")
    public ApiResult<CombinationStarRateDto> updateCombinationStarRate(
        @PathVariable Long combinationId,
        @PathVariable Long combinationStarRateId,
        @Valid @RequestBody CombinationStarRateRequest request) {
        return success(
            combinationStarRateService
                .updateCombinationStarRate(combinationId, combinationStarRateId, getMember(),
                    request)
        );
    }

    @DeleteMapping("{combinationStarRateId}")
    public ApiResult<Boolean> deleteCombinationStarRate(@PathVariable Long combinationStarRateId) {
        return success(combinationStarRateService
            .deleteCombinationStarRate(combinationStarRateId, getMember()));
    }


    //임의의 member 값
    private Member getMember() {
        return memberRepository.findById("testMemberId1")
            .orElseThrow(() -> new IllegalArgumentException("Member가 존재하지 않습니다,"));
    }
}
