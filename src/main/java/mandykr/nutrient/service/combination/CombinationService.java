package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CombinationService {
    Page<CombinationDto> getCombinations(CombinationSearchCondition condition, Pageable pageable);
    CombinationDetailDto createCombination(CombinationCreateRequest combinationCreateRequest);
    CombinationDetailDto updateCombination(Long id, CombinationUpdateRequest updateRequest);
}
