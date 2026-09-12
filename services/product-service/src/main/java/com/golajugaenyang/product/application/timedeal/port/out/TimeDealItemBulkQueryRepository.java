package com.golajugaenyang.product.application.timedeal.port.out;

import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealItemInternalProjection;
import java.util.List;

public interface TimeDealItemBulkQueryRepository {

    List<TimeDealItemInternalProjection> findAllByIds(List<Long> timeDealItemIds);
}
