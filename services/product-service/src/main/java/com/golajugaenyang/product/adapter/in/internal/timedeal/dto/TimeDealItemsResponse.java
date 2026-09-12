package com.golajugaenyang.product.adapter.in.internal.timedeal.dto;

import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealItemInternalLookupResult;
import java.util.List;

public record TimeDealItemsResponse(
    List<TimeDealItemInternalItemResponse> items,
    List<Long> missingTimeDealItemIds
) {

    public static TimeDealItemsResponse from(TimeDealItemInternalLookupResult result) {
        List<TimeDealItemInternalItemResponse> items = result.items()
            .stream()
            .map(TimeDealItemInternalItemResponse::from)
            .toList();
        return new TimeDealItemsResponse(items, result.missingTimeDealItemIds());
    }
}
