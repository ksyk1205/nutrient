package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.dto.supplement.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplementRepositoryCustom {
    List<SupplementSearchComboResponse> searchSupplementCombo(SupplementSearchComboRequest condition);
    Page<SupplementSearchResponse> searchSupplementList(SupplementSearchRequest condition, Pageable pageables);
}
