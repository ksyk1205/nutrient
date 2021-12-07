package mandykr.nutrient.repository;

import mandykr.nutrient.entity.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplementRepository extends JpaRepository<Supplement, Long>, SupplementRepositoryCustom {
    List<Supplement> findByName(String name);
}
