package com.golajugaenyang.product.adapter.out.persistence.product;


import static com.golajugaenyang.product.domain.product.QBrand.brand;
import static com.golajugaenyang.product.domain.product.QProduct.product;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.application.product.port.out.ProductSearchQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import com.golajugaenyang.product.application.product.port.out.dto.ProductSearchCriteria;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ProductSearchQueryRepositoryImpl implements ProductSearchQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductListProjection> search(ProductSearchCriteria criteria) {
        ProductSortType sort = criteria.effectiveSortType();

        BooleanBuilder where = baseWhere(criteria);
        BooleanExpression cursorCondition = ProductSortQuerySupport
            .cursorCondition(sort, criteria.cursor());
        if (cursorCondition != null) {
            where.and(cursorCondition);
        }

        return queryFactory
            .select(Projections.constructor(ProductListProjection.class,
                product.id,
                product.thumbnailUrl,
                product.productName,
                product.price,
                product.originalPrice,
                product.normalizedQuantityValue,
                product.normalizedQuantityUnit,
                product.avgRating,
                product.reviewCount,
                product.salesCount))
            .from(product)
            .leftJoin(brand)
            .on(product.brandId.eq(brand.id))
            .where(where)
            .orderBy(ProductSortQuerySupport.orderSpecifiers(sort))
            .limit(criteria.size() + 1L)
            .fetch();
    }

    @Override
    public long count(ProductSearchCriteria criteria) {
        Long total = queryFactory
            .select(product.count())
            .from(product)
            .leftJoin(brand).on(product.brandId.eq(brand.id))
            .where(baseWhere(criteria))
            .fetchOne();
        return total != null ? total : 0L;
    }

    private BooleanBuilder baseWhere(ProductSearchCriteria criteria) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(product.active.isTrue());
        if (criteria.category() != null) {
            where.and(product.categoryCode.eq(criteria.category()));
        }
        where.and(keywordCondition(criteria.keyword()));
        return where;
    }

    private BooleanExpression keywordCondition(String keyword) {
        String normalized = keyword.toLowerCase();

        BooleanExpression nameMatch = product.productName.lower().contains(normalized);
        BooleanExpression brandMatch = brand.name.lower().contains(normalized);
        BooleanExpression condition = nameMatch.or(brandMatch);

        Set<CategoryCode> matchedCategories = CategoryCode.matchByDisplayName(keyword);
        if (!matchedCategories.isEmpty()) {
            condition = condition.or(product.categoryCode.in(matchedCategories));
        }
        return condition;
    }
}
