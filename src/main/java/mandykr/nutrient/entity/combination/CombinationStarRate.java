package mandykr.nutrient.entity.combination;

import lombok.*;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.util.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class CombinationStarRate extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "COMBINATION_STAR_RATE_ID")
    private Long id;

    private Integer starNumber;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "COMBINATION_ID")
    private Combination combination;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void updateStarNumber(int starNumber) {
        this.starNumber = starNumber;
    }

    public void addStarRate(Combination combination) {
        this.combination = combination;
        if(!combination.getCombinationStarRates().contains(this)){
            combination.getCombinationStarRates().add(this);
        }
    }
}
