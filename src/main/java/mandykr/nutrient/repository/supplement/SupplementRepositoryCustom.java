package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.dto.supplement.SupplementDto;
import mandykr.nutrient.dto.SupplementSearchCondition;

import java.util.List;

public interface SupplementRepositoryCustom {
    List<SupplementDto> search(SupplementSearchCondition condition);
}
