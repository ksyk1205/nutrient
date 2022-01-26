package mandykr.nutrient.repository.combination;

import mandykr.nutrient.dto.combination.*;
import mandykr.nutrient.entity.combination.Combination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CombinationRepositoryCustom {
    Page<CombinationDto> findWithSupplement(Pageable pageable);
    Page<CombinationDto> searchBySupplementList(List<CombinationConditionSupplement> supplementList, Pageable pageable);
    Page<CombinationDto> searchByCategoryList(List<CombinationConditionCategory> categoryList, Pageable pageable);

    CombinationDetailDto searchByCombination(Combination saveCombination);
}
