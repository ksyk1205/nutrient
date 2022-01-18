package mandykr.nutrient.repository.supplement;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import mandykr.nutrient.dto.supplement.SupplementDto;
import mandykr.nutrient.dto.SupplementSearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static mandykr.nutrient.entity.QSupplement.*;

@Repository
public class SupplementRepositoryImpl implements SupplementRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public SupplementRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<SupplementDto> search(SupplementSearchCondition condition) {
        List<SupplementDto> result = queryFactory
                .select(Projections.fields(SupplementDto.class,
                        supplement.id,
                        supplement.id))
                .from(supplement)
                .where(supplementsNameEq(condition.getName()))
                .fetch();

        return result;
    }

    // supplementsName 비교 조건절
    public BooleanExpression supplementsNameEq(String supplementsNameCond) {
        return supplement.name.eq(String.valueOf(supplementsNameCond));
    }
}
