package mandykr.nutrient.controller.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.CombinationConditionCategory;
import mandykr.nutrient.dto.combination.CombinationConditionSupplement;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import mandykr.nutrient.service.combination.CombinationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static mandykr.nutrient.util.ApiUtils.ApiResult;
import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CombinationController {
    private final CombinationService combinationService;

    /**
     * 검색조건을 전달받아 영양제 조합 목록을 페이징으로 반환한다.
     *
     */
    @GetMapping("/combinations")
    public ApiResult<Page<CombinationDto>> getCombinations(
            @RequestBody List<CombinationConditionSupplement> conditionSupplementList,
            @RequestBody List<CombinationConditionCategory> conditionCategoryList,
            Pageable pageable) {
        CombinationSearchCondition condition = new CombinationSearchCondition(conditionSupplementList, conditionCategoryList);
        return success(combinationService.getCombinations(condition, pageable));
    }

}
