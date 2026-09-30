package com.golajugaenyang.product.adapter.out.persistence.timedeal;


import static com.golajugaenyang.product.domain.timedeal.QTimeDeal.timeDeal;

import com.golajugaenyang.product.application.timedeal.port.out.TimeDealTransitionRepository;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TimeDealTransitionRepositoryImpl implements TimeDealTransitionRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 시작 시각이 지난 SCHEDULED 딜을 ACTIVE로 전환
     */
    @Override
    public int activateScheduledDeals(OffsetDateTime now) {
        return (int) queryFactory.update(timeDeal)
            .set(timeDeal.status, TimeDealStatus.ACTIVE)
            .where(timeDeal.status.eq(TimeDealStatus.SCHEDULED).and(timeDeal.startAt.loe(now)))
            .execute();
    }

    /**
     * 종료 시각이 지난 ACTIVE 딜을 ENDED로 전환
     */
    @Override
    public int endActiveDeals(OffsetDateTime now) {
        return (int) queryFactory.update(timeDeal)
            .set(timeDeal.status, TimeDealStatus.ENDED)
            .where(timeDeal.status.eq(TimeDealStatus.ACTIVE).and(timeDeal.endAt.loe(now)))
            .execute();
    }
}
