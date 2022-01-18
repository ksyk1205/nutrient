package mandykr.nutrient.dto.combination;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CombinationSearchCondition {
    private List<CombinationConditionSupplement> supplementList = new ArrayList<>();
    private List<CombinationConditionCategory> categoryList = new ArrayList<>();

    public boolean isEmpty() {
        return supplementList.isEmpty()
                && categoryList.isEmpty();
    }
}
