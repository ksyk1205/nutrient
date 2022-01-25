package mandykr.nutrient.dto.supplement.starRate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
public class SupplementStarRateDto { //화면에 출력되는 값들
    private Long id; //영양제 별점 번호
    private int starNumber; //별점 갯수
    private Supplement supplement; //영양제 번호
    // 회원 번호
    //private Member member;

    //Entity -> Dto
    public SupplementStarRateDto(SupplementStarRate starRate){
        BeanUtils.copyProperties(starRate, this);
    }
}
