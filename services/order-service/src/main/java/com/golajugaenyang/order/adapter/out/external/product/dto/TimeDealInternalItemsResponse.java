package com.golajugaenyang.order.adapter.out.external.product.dto;

import java.util.List;

public record TimeDealInternalItemsResponse(
    List<TimeDealInternalItemResponse> items,
    List<Long> missingTimeDealItemIds
) {

}
