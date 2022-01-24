package mandykr.nutrient.dto.supplement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplementSearchRequest {
    private Long categoryId;
    private String supplementName;

}
