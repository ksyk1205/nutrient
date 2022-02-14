package mandykr.nutrient.repository.combination;

import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplementCombinationRepository extends JpaRepository<SupplementCombination, Long> {
}
