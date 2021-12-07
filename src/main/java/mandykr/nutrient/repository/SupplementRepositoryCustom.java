package mandykr.nutrient.repository;

import mandykr.nutrient.dto.SupplementDto;
import mandykr.nutrient.dto.SupplementSearchCondition;

import java.util.List;

public interface SupplementRepositoryCustom {
    List<SupplementDto> search(SupplementSearchCondition condition);
}
