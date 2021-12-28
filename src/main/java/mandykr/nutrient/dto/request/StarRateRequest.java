package mandykr.nutrient.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class StarRateRequest { //화면에 입력받는 값들
    @Max(value = 5)
    @Min(value = 0)
    private int starNumber; //별점 갯수
}
