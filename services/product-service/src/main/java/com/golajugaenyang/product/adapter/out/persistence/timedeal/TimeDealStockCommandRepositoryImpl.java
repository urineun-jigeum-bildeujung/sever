package com.golajugaenyang.product.adapter.out.persistence.timedeal;


import static com.golajugaenyang.product.domain.timedeal.QTimeDealItem.timeDealItem;

import com.golajugaenyang.product.application.inventory.port.out.TimeDealStockCommandRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class TimeDealStockCommandRepositoryImpl implements TimeDealStockCommandRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public int reserve(Long timeDealItemId, int quantity) {
        return (int) queryFactory.update(timeDealItem)
            .set(timeDealItem.reservedQuantity, timeDealItem.reservedQuantity.add(quantity))
            .where(timeDealItem.id.eq(timeDealItemId)
                .and(timeDealItem.quantityLimit
                    .subtract(timeDealItem.reservedQuantity)
                    .subtract(timeDealItem.soldQuantity).goe(quantity)))
            .execute();
    }

    @Override
    public int confirm(Long timeDealItemId, int quantity) {
        return (int) queryFactory.update(timeDealItem)
            .set(timeDealItem.reservedQuantity, timeDealItem.reservedQuantity.subtract(quantity))
            .set(timeDealItem.soldQuantity, timeDealItem.soldQuantity.add(quantity))
            .where(
                timeDealItem.id.eq(timeDealItemId).and(timeDealItem.reservedQuantity.goe(quantity)))
            .execute();
    }

    @Override
    public int release(Long timeDealItemId, int quantity) {
        return (int) queryFactory.update(timeDealItem)
            .set(timeDealItem.reservedQuantity, timeDealItem.reservedQuantity.subtract(quantity))
            .where(
                timeDealItem.id.eq(timeDealItemId).and(timeDealItem.reservedQuantity.goe(quantity)))
            .execute();
    }

    @Override
    public int restore(Long timeDealItemId, int quantity) {
        return (int) queryFactory.update(timeDealItem)
            .set(timeDealItem.soldQuantity, timeDealItem.soldQuantity.subtract(quantity))
            .where(timeDealItem.id.eq(timeDealItemId).and(timeDealItem.soldQuantity.goe(quantity)))
            .execute();
    }
}
