package mandykr.nutrient.repository.supplement.reply;

import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementReplyRepository extends JpaRepository<SupplementReply,Long>{
    @Query("select s from SupplementReply s where s.parent is null and s.supplement = :supplement")
    Page<SupplementReply> findBySupplementWithParent(@Param("supplement")Supplement supplement,Pageable pageable);

    @Query("select s from SupplementReply s where s.parent is not null and s.supplement = :supplement and s.parent = :supplementReply")
    Page<SupplementReply> findBySupplementWithChild(@Param("supplement")Supplement supplement, @Param("supplementReply")SupplementReply supplementReply, Pageable pageable);

    @Query("select MAX(s.groups) from SupplementReply s left join s.parent where s.parent is null and s.supplement.id = :supplementId")
    Long findByParentLastGroup(@Param("supplementId") Long supplementId);

    @Query("select MAX(s.groupOrder) from SupplementReply s left join s.parent where s.parent.id = :supplementReplyId and s.supplement.id = :supplementId")
    Long findByChildLastGroupOrder(@Param("supplementId") Long supplementId , @Param("supplementReplyId") Long supplementReplyId);

    Optional<SupplementReply> findByIdAndMember(@Param("supplementId") Long supplementReplyId, @Param("memberId") Member member);


}