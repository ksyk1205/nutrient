package mandykr.nutrient.repository;

import mandykr.nutrient.entity.Supplements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplementsRepository extends JpaRepository<Supplements, Long>, SupplementsRepositoryCustom {
    List<Supplements> findByName(String name);
}
