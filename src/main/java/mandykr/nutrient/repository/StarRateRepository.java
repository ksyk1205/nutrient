package mandykr.nutrient.repository;

import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarRateRepository extends JpaRepository<StarRate,Long> {
    //영양제번호로 별점리스트 조회
    List<StarRate> findBySupplement(Supplement supplement);
    //영양제번호와 회원번호로 별점 조회
    Optional<StarRate> findBySupplementAndMember(Supplement supplement,Member member);
}
