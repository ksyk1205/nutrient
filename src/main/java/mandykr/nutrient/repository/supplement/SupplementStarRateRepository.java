package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementStarRateRepository extends JpaRepository<SupplementStarRate,Long> {

    List<SupplementStarRate> findBySupplement(Supplement supplement);

    Optional<SupplementStarRate> findBySupplementAndMember(Supplement supplement, Member member);

    Optional<SupplementStarRate> findByMemberAndId(@Param("member") Member member, @Param("id") Long starRateId);
}
