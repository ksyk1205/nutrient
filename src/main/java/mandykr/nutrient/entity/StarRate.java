package mandykr.nutrient.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mandykr.nutrient.dto.StarRateDto;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class StarRate {
    @Id
    @GeneratedValue
    @Column(name = "STAR_RATE_ID")
    private Long id; //영양제 별점 번호

    private Long starNumber; //별점 갯수

    @ManyToOne
    @JoinColumn(name = "SUPPLEMENT_ID")
    private Supplement supplement; //영양제 번호

    // 회원 번호
    //private Member member;

    public static StarRateDto makeStarRateDto(StarRate starRate){
        StarRateDto starRateDto = new StarRateDto();
        starRateDto.setId(starRate.getId());
        starRateDto.setStarNumber(starRate.getStarNumber());
        starRateDto.setSupplement(starRate.getSupplement());
        return starRateDto;
    }
}
