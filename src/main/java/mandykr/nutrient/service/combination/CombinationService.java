package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CombinationService {
    Page<CombinationDto> getCombinations(CombinationSearchCondition condition, Pageable pageable);
}
