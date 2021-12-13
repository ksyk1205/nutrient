package mandykr.nutrient.repository;

import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.entity.SupplementReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplementReplyRepository extends JpaRepository<SupplementReply,Long> {
    List<SupplementReply> findBySupplementId(Long supplementId);
}
