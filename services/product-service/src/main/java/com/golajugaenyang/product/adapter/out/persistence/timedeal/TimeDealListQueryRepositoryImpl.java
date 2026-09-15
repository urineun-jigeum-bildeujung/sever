package com.golajugaenyang.product.adapter.out.persistence.timedeal;


import static com.golajugaenyang.product.domain.product.QProduct.product;
import static com.golajugaenyang.product.domain.timedeal.QTimeDeal.timeDeal;
import static com.golajugaenyang.product.domain.timedeal.QTimeDealItem.timeDealItem;

import com.golajugaenyang.product.application.timedeal.port.out.TimeDealListQueryRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealListRowProjection;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class TimeDealListQueryRepositoryImpl implements TimeDealListQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TimeDealListRowProjection> findItemsByDealStatus(TimeDealStatus status) {
        return queryFactory
            .select(Projections.constructor(TimeDealListRowProjection.class,
                timeDeal.id, timeDeal.name, timeDeal.startAt, timeDeal.endAt,
                timeDealItem.id, product.id, product.thumbnailUrl, product.productName,
                timeDealItem.normalPrice,
                timeDealItem.discountedPrice, timeDealItem.discountRate,
                product.normalizedQuantityValue, product.normalizedQuantityUnit,
                timeDealItem.quantityLimit, timeDealItem.reservedQuantity,
                timeDealItem.soldQuantity,
                timeDealItem.itemStatus))
            .from(timeDealItem)
            .join(timeDeal).on(timeDealItem.dealId.eq(timeDeal.id))
            .join(product).on(timeDealItem.productId.eq(product.id))
            .where(timeDeal.status.eq(status))
            .orderBy(timeDeal.startAt.asc(), timeDealItem.id.asc())
            .fetch();
    }
}
