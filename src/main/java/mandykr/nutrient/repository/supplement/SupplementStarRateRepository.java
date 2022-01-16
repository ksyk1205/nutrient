package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementStarRateRepository extends JpaRepository<SupplementStarRate,Long> {
    //영양제번호로 별점리스트 조회
    List<SupplementStarRate> findBySupplement(Supplement supplement);
    //영양제번호와 회원번호로 별점 조회
    Optional<SupplementStarRate> findBySupplementAndMember(Supplement supplement, Member member);
}
