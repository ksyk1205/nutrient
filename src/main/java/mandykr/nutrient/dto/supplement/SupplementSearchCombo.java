package mandykr.nutrient.dto.supplement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupplementSearchCombo {
    private String name;

    public SupplementSearchCombo(String name) {
        this.name = name;
    }

}
