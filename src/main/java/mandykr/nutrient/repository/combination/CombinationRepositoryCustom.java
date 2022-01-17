package mandykr.nutrient.repository.combination;

import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CombinationRepositoryCustom {
    Page<CombinationDto> findWithSupplement(Pageable pageable);
    Page<CombinationDto> searchWithSupplement(CombinationSearchCondition condition, Pageable pageable);
}
