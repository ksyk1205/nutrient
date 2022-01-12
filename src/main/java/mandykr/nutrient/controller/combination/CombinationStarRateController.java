package mandykr.nutrient.controller.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.CombinationStarRateDto;
import mandykr.nutrient.dto.request.CombineStarRateRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.CombinationStarRateService;
import mandykr.nutrient.util.ApiUtils;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequestMapping("/combine-star-rate")
@RequiredArgsConstructor
public class CombinationStarRateController {

    private final CombinationStarRateService combinationStarRateService;

    private final MemberRepository memberRepository;


    @GetMapping("{combinationId}")
    public ApiResult<CombinationStarRateDto> getCombinationStarRate(@PathVariable Long combinationId){
        return success(
                combinationStarRateService
                .getCombineStarRateByCombine(combinationId,getMember())
                .map(CombinationStarRateDto::new)
                .get()
        );
    }

    @PostMapping("{combinationId}")
    public ApiResult<CombinationStarRateDto> saveCombinationStarRate(@PathVariable Long combinationId,
                                                                     @Valid @RequestBody CombineStarRateRequest request){
        return success(
                combinationStarRateService
                        .saveCombinationStarRate(combinationId,getMember(), request)
                        .map(CombinationStarRateDto::new)
                        .get()
        );
    }
    @PutMapping("{combinationId}/{combinationStarRateId}")
    public ApiResult<CombinationStarRateDto> updateCombinationStarRate(@PathVariable Long combinationId,
                                                                       @PathVariable Long combinationStarRateId,
                                                                       @Valid @RequestBody CombineStarRateRequest request){
        return success(
                combinationStarRateService
                        .updateCombinationStarRate(combinationId, combinationStarRateId, getMember(), request)
                        .map(CombinationStarRateDto::new)
                        .get()
        );
    }

    @DeleteMapping("{combinationStarRateId}")
    public void deleteCombinationStarRate(@PathVariable Long combinationStarRateId){
        combinationStarRateService.deleteCombinationStarRate(combinationStarRateId, getMember());
    }


    //임의의 member 값
    private Member getMember() {
        Member member = memberRepository.findById("testMemberId1").get();
        return member;
    }
}
