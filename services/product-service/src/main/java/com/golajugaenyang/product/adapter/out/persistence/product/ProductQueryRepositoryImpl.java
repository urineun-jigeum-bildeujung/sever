package com.golajugaenyang.product.adapter.out.persistence.product;


import static com.golajugaenyang.product.domain.product.QProduct.product;

import com.golajugaenyang.product.application.product.port.out.dto.ProductListCriteria;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import com.golajugaenyang.product.application.product.port.out.ProductQueryRepository;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductListProjection> findProductList(ProductListCriteria criteria) {
        ProductSortType sort = criteria.sortType().resolveEffectiveSort();

        BooleanBuilder where = new BooleanBuilder();
        where.and(product.active.isTrue());
        if (criteria.category() != null) {
            where.and(product.categoryCode.eq(criteria.category()));
        }
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
            .where(where)
            .orderBy(ProductSortQuerySupport.orderSpecifiers(sort))
            .limit(criteria.size() + 1L)
            .fetch();
    }
}
