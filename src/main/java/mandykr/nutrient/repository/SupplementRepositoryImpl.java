package mandykr.nutrient.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import mandykr.nutrient.dto.SupplementsDto;
import mandykr.nutrient.dto.SupplementsSearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static mandykr.nutrient.entity.QSupplements.*;

@Repository
public class SupplementsRepositoryImpl implements SupplementsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public SupplementsRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<SupplementsDto> search(SupplementsSearchCondition condition) {
        List<SupplementsDto> result = queryFactory
                .select(Projections.fields(SupplementsDto.class,
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
