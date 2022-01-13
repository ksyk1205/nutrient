package mandykr.nutrient.dto.combination.starRate.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class CombineStarRateRequest {
    @Max(value = 5)
    @Min(value = 0)
    private int starNumber; //별점 갯수
}
