package mandykr.nutrient.dto.combination.starRate.request;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinationStarRateRequest {
    @Max(value = 5)
    @Min(value = 0)
    private int starNumber; //별점 갯수
}
