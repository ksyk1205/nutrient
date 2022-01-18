package mandykr.nutrient.dto.combination.starRate;

import lombok.Data;
import mandykr.nutrient.dto.combination.starRate.request.CombinationStarRateRequest;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
public class CombinationStarRateRequestDto {
    private int starNumber; //별점 갯수

    public CombinationStarRateRequestDto(CombinationStarRateRequest source) {
        copyProperties(source, this);
    }

}
