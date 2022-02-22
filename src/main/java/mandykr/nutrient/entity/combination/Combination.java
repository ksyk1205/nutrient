package mandykr.nutrient.entity.combination;

import lombok.*;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.util.BaseTimeEntity;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Combination extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "COMBINATION_ID")
    private Long id;

    private String caption;

    private double rating;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "combination")
    @Builder.Default
    private List<CombinationStarRate> combinationStarRates = new ArrayList<>();

    @OneToMany(mappedBy = "combination", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @Builder.Default
    private List<SupplementCombination> supplementCombinations = new ArrayList<>();

    public Combination(Long id) {
        this.id = id;
    }

    public void updateRating() {
        this.rating = combinationStarRates.stream().mapToInt(o -> o.getStarNumber()).average().getAsDouble();
    }

    public void updateList(CombinationStarRate combinationStarRate) {
        int idx = combinationStarRates.indexOf(combinationStarRate);
        combinationStarRates.set(idx, combinationStarRate);
    }

    public void editCaption(String caption) {
        this.caption = caption;
    }

    public void removeSupplementCombinations(List<Long> supplementIds) {
        supplementCombinations = supplementCombinations.stream()
                .filter(c -> supplementIds.stream()
                        .anyMatch(id -> id.equals(c.getSupplement().getId())))
                .collect(Collectors.toList());
    }

    public void addSupplementCombinations(List<Supplement> supplements) {
        supplements.forEach(s -> {
            if (doesNotContains(s)) {
                supplementCombinations.add(new SupplementCombination(s, this));
            }
        });
    }

    private boolean doesNotContains(Supplement supplement) {
        return supplementCombinations.stream()
                .noneMatch(c -> supplement.equals(c.getSupplement()));
    }
}
