package mandykr.nutrient.repository.combination;

import mandykr.nutrient.dto.combination.CombinationConditionCategory;
import mandykr.nutrient.dto.combination.CombinationConditionSupplement;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import mandykr.nutrient.entity.Supplement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CombinationRepositoryCustom {
    Page<CombinationDto> findWithSupplement(Pageable pageable);
    Page<CombinationDto> searchBySupplementList(List<CombinationConditionSupplement> supplementList, Pageable pageable);
    Page<CombinationDto> searchByCategoryList(List<CombinationConditionCategory> categoryList, Pageable pageable);
}
