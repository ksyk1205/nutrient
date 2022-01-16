package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.entity.supplement.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplementRepository extends JpaRepository<Supplement, Long>, SupplementRepositoryCustom {

    List<Supplement> findByName(String name);
}
