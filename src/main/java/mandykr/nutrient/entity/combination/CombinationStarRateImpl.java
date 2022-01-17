package mandykr.nutrient.entity.combination;

import mandykr.nutrient.entity.Member;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

public interface CombinationStarRateImpl {
    void updateStarNumber(int starNumber);

    void addStarRate(Combination combination);

    CombinationStarRate NULL = new CombinationStarRate(){

    };
}
