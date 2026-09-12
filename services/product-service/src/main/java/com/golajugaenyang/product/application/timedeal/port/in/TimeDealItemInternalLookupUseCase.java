package com.golajugaenyang.product.application.timedeal.port.in;

import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealItemInternalLookupResult;
import java.util.List;

public interface TimeDealItemInternalLookupUseCase {

    TimeDealItemInternalLookupResult getTimeDealItems(List<Long> timeDealItemIds);
}
