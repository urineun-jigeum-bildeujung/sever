package com.golajugaenyang.product.application.timedeal.port.out;

import java.time.OffsetDateTime;

public interface TimeDealTransitionRepository {

    /**
     * @return 진행중으로 전환된 딜 수
     */
    int activateScheduledDeals(OffsetDateTime now);

    /**
     * @return 종료로 전환된 딜 수
     */
    int endActiveDeals(OffsetDateTime now);
}
