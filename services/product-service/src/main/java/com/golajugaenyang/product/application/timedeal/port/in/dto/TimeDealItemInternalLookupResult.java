package com.golajugaenyang.product.application.timedeal.port.in.dto;

import java.util.List;

public record TimeDealItemInternalLookupResult(
    List<TimeDealItemInternalItem> items,
    List<Long> missingTimeDealItemIds
) {

}
