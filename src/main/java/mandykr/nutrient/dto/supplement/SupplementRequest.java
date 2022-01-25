package mandykr.nutrient.dto.supplement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplementRequest {
    @NotEmpty
    private String name;
    @NotEmpty
    private String prdlstReportNo;
}
