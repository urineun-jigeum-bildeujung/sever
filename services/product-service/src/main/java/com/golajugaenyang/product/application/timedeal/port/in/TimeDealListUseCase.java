package com.golajugaenyang.product.application.timedeal.port.in;

import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealListResult;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;

public interface TimeDealListUseCase {

    TimeDealListResult getTimeDeals(TimeDealStatus status);
}
