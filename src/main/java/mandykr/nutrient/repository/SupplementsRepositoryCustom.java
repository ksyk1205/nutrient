package mandykr.nutrient.repository;

import mandykr.nutrient.dto.SupplementsDto;
import mandykr.nutrient.dto.SupplementsSearchCondition;

import java.util.List;

public interface SupplementsRepositoryCustom {
    List<SupplementsDto> search(SupplementsSearchCondition condition);
}
