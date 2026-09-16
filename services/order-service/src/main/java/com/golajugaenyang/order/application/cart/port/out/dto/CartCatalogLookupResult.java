package com.golajugaenyang.order.application.cart.port.out.dto;

import com.golajugaenyang.order.domain.cart.CartItemType;
import java.util.List;
import java.util.Map;

public record CartCatalogLookupResult(
    Map<Long, ProductSummary> products,
    Map<Long, TimeDealSummary> timeDealItems,
    List<Long> missingProductIds,
    List<Long> missingTimeDealItemIds,
    List<Long> unreachableProductIds,
    List<Long> unreachableTimeDealItemIds
) {

    public boolean isUnreachable(CartItemType itemType, Long itemId) {
        return itemType == CartItemType.NORMAL
            ? unreachableProductIds.contains(itemId)
            : unreachableTimeDealItemIds.contains(itemId);
    }
}
