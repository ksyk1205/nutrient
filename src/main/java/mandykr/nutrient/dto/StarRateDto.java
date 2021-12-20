package mandykr.nutrient.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
public class StarRateDto {
    private Long id; //영양제 별점 번호
    private Long starNumber; //별점 갯수
    private Supplement supplement; //영양제 번호
    // 회원 번호
    //private Member member;

    //Entity -> Dto
    public StarRateDto(StarRate starRate){
        BeanUtils.copyProperties(starRate,this);
    }
}
