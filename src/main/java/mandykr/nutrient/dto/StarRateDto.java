package mandykr.nutrient.dto;

import lombok.Getter;
import lombok.Setter;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;

@Getter
@Setter
public class StarRateDto {
    private Long id; //영양제 별점 번호
    private Long starNumber; //별점 갯수
    private Supplement supplement; //영양제 번호
    // 회원 번호
    //private Member member;

    public static StarRate makeStarRate(StarRateDto starRateDto){
        StarRate starRate = new StarRate();
        starRate.setId(starRateDto.getId());
        starRate.setStarNumber(starRateDto.getStarNumber());
        starRate.setSupplement(starRateDto.getSupplement());
        return starRate;
    }
}
