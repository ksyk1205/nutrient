package mandykr.nutrient.dto.combination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombinationCreateRequest {
    @NotEmpty
    @Size(min = 1, max = 50)
    String caption;

    @Size(min=1)
    List<Long> supplementIds = new ArrayList<>();

    public CombinationCreateDto of() {
        return new CombinationCreateDto(this.caption, this.supplementIds);
    }
}
