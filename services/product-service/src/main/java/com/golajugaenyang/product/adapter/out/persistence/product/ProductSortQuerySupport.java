package com.golajugaenyang.product.adapter.out.persistence.product;

import static com.golajugaenyang.product.domain.product.QProduct.product;

import com.golajugaenyang.product.application.product.port.out.dto.PageCursor;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.math.BigDecimal;


final class ProductSortQuerySupport {

    private ProductSortQuerySupport() {
    }

    static BooleanExpression cursorCondition(ProductSortType sort, PageCursor cursor) {
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

    static OrderSpecifier<?>[] orderSpecifiers(ProductSortType sort) {
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
