package com.golajugaenyang.product.adapter.out.persistence.timedeal;


import static com.golajugaenyang.product.domain.product.QProduct.product;
import static com.golajugaenyang.product.domain.timedeal.QTimeDeal.timeDeal;
import static com.golajugaenyang.product.domain.timedeal.QTimeDealItem.timeDealItem;

import com.golajugaenyang.product.application.timedeal.port.out.TimeDealItemBulkQueryRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealItemInternalProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TimeDealItemBulkQueryRepositoryImpl implements TimeDealItemBulkQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TimeDealItemInternalProjection> findAllByIds(List<Long> timeDealItemIds) {
        return queryFactory
            .select(Projections.constructor(TimeDealItemInternalProjection.class,
                timeDealItem.id, timeDealItem.dealId, timeDealItem.productId,
                product.productGroupId, product.thumbnailUrl, product.productName,
                product.categoryCode, product.replenishable,
                timeDealItem.normalPrice, timeDealItem.discountedPrice,
                timeDealItem.discountRate,
                product.netQuantityValue, product.netQuantityUnit,
                product.quantityDimension, product.normalizedQuantityValue,
                product.normalizedQuantityUnit,
                timeDealItem.quantityLimit, timeDealItem.reservedQuantity,
                timeDealItem.soldQuantity,
                timeDealItem.perUserQuantityLimit,
                timeDealItem.itemStatus, timeDeal.status, timeDeal.endAt))
            .from(timeDealItem)
            .join(timeDeal).on(timeDealItem.dealId.eq(timeDeal.id))
            .join(product).on(timeDealItem.productId.eq(product.id))
            .where(timeDealItem.id.in(timeDealItemIds))
            .fetch();
    }
}
