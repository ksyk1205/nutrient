package mandykr.nutrient.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SupplementCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUPPLEMENT_COMBINATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLEMENT_ID")
    private Supplement supplement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMBINATION_ID")
    private Combination combination;

    public SupplementCombination(Supplement supplement, Combination combination) {
        this.supplement = supplement;
        this.combination = combination;
    }
}
