package mandykr.nutrient.repository.supplement.reply;

import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementReplyRepository extends JpaRepository<SupplementReply,Long> {
    List<SupplementReply> findBySupplementAndParentIsNull(Supplement supplement, Sort sort);

    @Query("select MAX(s.groups) from SupplementReply s left join s.parent where s.parent is null and s.supplement.id = :supplementId")
    Long findByLastOrderWithParent(@Param("supplementId") Long supplementId);

    @Query("select MAX(s.groupOrder) from SupplementReply s left join s.parent where s.parent.id = :supplementReplyId and s.supplement.id = :supplementId")
    Long findByLastOrderWithChild(@Param("supplementId") Long supplementId , @Param("supplementReplyId") Long supplementReplyId);

    Optional<SupplementReply> findByIdAndMember(@Param("supplementId") Long supplementReplyId, @Param("memberId") Member member);

    List<SupplementReply> findBySupplementAndParent(Supplement supplement, SupplementReply supplementReply, Sort sort);
}