package mandykr.nutrient.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import mandykr.nutrient.dto.SupplementDto;
import mandykr.nutrient.dto.SupplementSearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static mandykr.nutrient.entity.QSupplements.*;

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
                        supplements.id,
                        supplements.id))
                .from(supplements)
                .where(supplementsNameEq(condition.getName()))
                .fetch();

        return result;
    }

    // supplementsName 비교 조건절
    public BooleanExpression supplementsNameEq(String supplementsNameCond) {
        return supplements.name.eq(String.valueOf(supplementsNameCond));
    }
}
