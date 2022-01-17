package mandykr.nutrient.dto.combination.starRate;

import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.dto.combination.starRate.request.CombinationStarRateRequest;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.combination.CombinationStarRateImpl;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
@NoArgsConstructor
public class CombinationStarRateResponseDto {
    private Long id; //별점 번호
    private Integer starNumber; //별점 갯수

    public CombinationStarRateResponseDto(CombinationStarRateImpl source) {
        copyProperties(source, this);
    }

}
