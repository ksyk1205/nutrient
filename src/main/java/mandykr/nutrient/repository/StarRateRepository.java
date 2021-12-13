package mandykr.nutrient.repository;

import mandykr.nutrient.entity.StarRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarRateRepository extends JpaRepository<StarRate,Long> {

}
