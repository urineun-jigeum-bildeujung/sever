package com.golajugaenyang.product.application.timedeal.port.out;

import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealListRowProjection;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import java.util.List;

public interface TimeDealListQueryRepository {

    List<TimeDealListRowProjection> findItemsByDealStatus(TimeDealStatus status);
}
