package mandykr.nutrient.dto.combination.starRate;

import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.combination.CombinationStarRate;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
@NoArgsConstructor
public class CombinationStarRateDto {
    private Long id; //별점 번호
    private Integer starNumber; //별점 갯수

    public CombinationStarRateDto(CombinationStarRate source) {
        copyProperties(source, this);
    }

}
