package mandykr.nutrient.entity.combination;

import mandykr.nutrient.entity.Member;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

public interface CombinationStarRateImpl {
    void updateStarNumber(int starNumber);

    void addStarRate(Combination combination);

    public static final CombinationStarRateImpl NULL = new CombinationStarRateImpl() {
        private Long id;
        private Integer starNumber;


        @Override
        public void updateStarNumber(int starNumber) {

        }
        @Override
        public void addStarRate(Combination combination) {

        }
    };
}
