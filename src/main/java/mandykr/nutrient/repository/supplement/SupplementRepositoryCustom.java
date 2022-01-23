package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.dto.supplement.*;

import java.util.List;

public interface SupplementRepositoryCustom {
    List<SupplementSearchComboResponse> searchCombo(SupplementSearchCombo condition);
    List<SupplementSearchResponse> searchSupplementList(SupplementSearch condition);
}
