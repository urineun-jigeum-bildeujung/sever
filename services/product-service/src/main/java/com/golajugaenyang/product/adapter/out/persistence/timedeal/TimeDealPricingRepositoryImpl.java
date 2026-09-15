package com.golajugaenyang.product.adapter.out.persistence.timedeal;


import static com.golajugaenyang.product.domain.timedeal.QTimeDeal.timeDeal;
import static com.golajugaenyang.product.domain.timedeal.QTimeDealItem.timeDealItem;

import com.golajugaenyang.product.application.timedeal.port.out.TimeDealPricingRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealPricingProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class TimeDealPricingRepositoryImpl implements TimeDealPricingRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<TimeDealPricingProjection> findByTimeDealItemId(Long timeDealItemId) {
        TimeDealPricingProjection result = queryFactory
            .select(Projections.constructor(TimeDealPricingProjection.class,
                timeDealItem.id, timeDealItem.dealId, timeDealItem.productId,
                timeDealItem.normalPrice, timeDealItem.discountedPrice,
                timeDealItem.discountRate,
                timeDealItem.quantityLimit, timeDealItem.reservedQuantity,
                timeDealItem.soldQuantity,
                timeDealItem.itemStatus, timeDeal.status))
            .from(timeDealItem)
            .join(timeDeal).on(timeDealItem.dealId.eq(timeDeal.id))
            .where(timeDealItem.id.eq(timeDealItemId))
            .fetchOne();
        return Optional.ofNullable(result);
    }
}
