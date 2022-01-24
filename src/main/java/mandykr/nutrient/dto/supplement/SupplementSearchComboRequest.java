package mandykr.nutrient.dto.supplement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupplementSearchComboRequest {
    private String name;

    public SupplementSearchComboRequest(String name) {
        this.name = name;
    }

}
