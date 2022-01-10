package mandykr.nutrient.entity.combination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
public class Combination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "combination_id")
    private Long id;

    public Combination(Long id) {
        this.id = id;
    }
}
