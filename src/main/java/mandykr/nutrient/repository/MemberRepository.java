package mandykr.nutrient.repository;

import mandykr.nutrient.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,String> {
}
