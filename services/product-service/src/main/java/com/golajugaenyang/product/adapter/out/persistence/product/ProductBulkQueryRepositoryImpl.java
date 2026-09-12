package com.golajugaenyang.product.adapter.out.persistence.product;


import static com.golajugaenyang.product.domain.product.QProduct.product;

import com.golajugaenyang.product.application.product.port.out.ProductBulkQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.ProductInternalProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductBulkQueryRepositoryImpl implements ProductBulkQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductInternalProjection> findAllByIds(List<Long> productIds) {
        return queryFactory
            .select(Projections.constructor(ProductInternalProjection.class,
                product.id, product.productGroupId, product.thumbnailUrl, product.productName,
                product.categoryCode, product.replenishable,
                product.price, product.originalPrice,
                product.netQuantityValue, product.netQuantityUnit,
                product.quantityDimension,
                product.normalizedQuantityValue, product.normalizedQuantityUnit,
                product.status))
            .from(product)
            .where(product.id.in(productIds))
            .fetch();
    }
}
