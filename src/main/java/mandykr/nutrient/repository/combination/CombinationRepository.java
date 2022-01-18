package mandykr.nutrient.repository.combination;

import mandykr.nutrient.entity.combination.Combination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CombinationRepository extends JpaRepository<Combination, Long>, CombinationRepositoryCustom {
    @Query("select c from Combination c left join fetch c.combinationStarRates where c.id = :id")
    Optional<Combination> findByIdFetch(@Param("id") Long combinationId);
}
