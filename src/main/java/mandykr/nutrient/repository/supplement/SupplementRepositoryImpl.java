package mandykr.nutrient.repository.supplement;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import mandykr.nutrient.dto.supplement.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.bean;
import static mandykr.nutrient.entity.QSupplementCategory.supplementCategory;
import static mandykr.nutrient.entity.supplement.QSupplement.*;

@Repository
public class SupplementRepositoryImpl implements SupplementRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public SupplementRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<SupplementSearchResponse> searchSupplementList(SupplementSearchRequest condition, Pageable pageable) {
        List<SupplementSearchResponse> result = queryFactory
                .select(
                    Projections.constructor(SupplementSearchResponse.class,
                        supplement.id,
                        supplement.name,
                        supplement.prdlstReportNo,
                        supplement.ranking,
                        supplement.deleteFlag,
                        Projections.constructor(SupplementSearchResponse.SupplementCategoryDto.class,
                                supplementCategory.id,
                                supplementCategory.name
                        )
                ))
                .from(supplement)
                .join(supplement.supplementCategory, supplementCategory)
                .where(
                        categoryEq(condition.getCategoryId()),
                        supplementsNameEq(condition.getSupplementName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                ;

        return new PageImpl<>(result, pageable, result.size());
    }

    private Predicate categoryEq(Long categoryId) {
        return  categoryId==null ? null : supplementCategory.id.eq(categoryId);
    }

    @Override
    public List<SupplementSearchComboResponse> searchSupplementCombo(SupplementSearchComboRequest condition) {
        List<SupplementSearchComboResponse> result = queryFactory
                .select(Projections.constructor(SupplementSearchComboResponse.class,
                        supplement.id,
                        supplement.name))
                .from(supplement)
                .where(supplementsNameEq(condition.getName()))
                .fetch();

        return result;
    }

    // supplementsName 비교 조건절
    public BooleanExpression supplementsNameEq(String supplementsNameCond) {
        return StringUtils.isEmpty(supplementsNameCond) ? null :supplement.name.contains(supplementsNameCond);
    }


}
