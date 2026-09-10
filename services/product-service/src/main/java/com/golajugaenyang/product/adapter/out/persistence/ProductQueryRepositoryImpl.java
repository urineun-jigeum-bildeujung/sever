package com.golajugaenyang.product.adapter.out.persistence;


import static com.golajugaenyang.product.domain.product.QProduct.product;

import com.golajugaenyang.product.application.product.port.out.dto.ProductCursor;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListCriteria;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import com.golajugaenyang.product.application.product.port.out.ProductQueryRepository;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
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
        BooleanExpression cursorCondition = cursorCondition(sort, criteria.cursor());
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
            .orderBy(orderSpecifiers(sort))
            .limit(criteria.size() + 1L)
            .fetch();
    }

    private BooleanExpression cursorCondition(ProductSortType sort, ProductCursor cursor) {
        if (cursor == null) {
            return null;
        }
        Long cursorId = cursor.id();
        return switch (sort) {
            case POPULAR -> {
                int v = cursor.sortValueAsInt();
                yield product.salesCount.lt(v)
                    .or(product.salesCount.eq(v).and(product.id.lt(cursorId)));
            }
            case REVIEW -> {
                int v = cursor.sortValueAsInt();
                yield product.reviewCount.lt(v)
                    .or(product.reviewCount.eq(v).and(product.id.lt(cursorId)));
            }
            case PRICE_DESC -> {
                BigDecimal v = cursor.sortValueAsBigDecimal();
                yield product.price.lt(v)
                    .or(product.price.eq(v).and(product.id.lt(cursorId)));
            }
            case PRICE_ASC -> {
                BigDecimal v = cursor.sortValueAsBigDecimal();
                yield product.price.gt(v)
                    .or(product.price.eq(v).and(product.id.gt(cursorId)));
            }
            case RECOMMEND -> throw new IllegalStateException("추천 시스템 연동 필요");
        };
    }

    private OrderSpecifier<?>[] orderSpecifiers(ProductSortType sort) {
        return switch (sort) {
            case POPULAR -> new OrderSpecifier<?>[]{product.salesCount.desc(), product.id.desc()};
            case REVIEW -> new OrderSpecifier<?>[]{product.reviewCount.desc(), product.id.desc()};
            case PRICE_DESC -> new OrderSpecifier<?>[]{product.price.desc(), product.id.desc()};
            case PRICE_ASC -> new OrderSpecifier<?>[]{product.price.asc(), product.id.asc()};
            // TODO: 추천 시스템 연동 후 수정
            case RECOMMEND -> new OrderSpecifier<?>[]{product.salesCount.desc(), product.id.desc()};
        };
    }
}
