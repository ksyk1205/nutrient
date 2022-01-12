package mandykr.nutrient.repository.combination.starrate;

import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CombinationStarRateRepository extends JpaRepository<CombinationStarRate, Long> {
    Optional<CombinationStarRate> findByCombinationIdAndMember(@Param("combinationId") Long combinationId,
                                                               @Param("memberId") Member member);

    @Query("select c from CombinationStarRate c where c.id = :id and c.member = :member and c.combination = :combination")
    Optional<CombinationStarRate> findIdAndMemberAndComb(@Param("id") Long id,
                                                         @Param("member") Member member,
                                                         @Param("combination") Long combinationId);
}
