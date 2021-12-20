package mandykr.nutrient.repository;

import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StarRateRepository extends JpaRepository<StarRate,Long> {
    List<StarRate> findBySupplement(Supplement supplement);
}
