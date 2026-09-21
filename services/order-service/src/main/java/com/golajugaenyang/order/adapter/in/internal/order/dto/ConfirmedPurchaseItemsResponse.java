package com.golajugaenyang.order.adapter.in.internal.order.dto;

import java.util.List;

public record ConfirmedPurchaseItemsResponse(
    List<ConfirmedPurchaseItemResponse> items
) {

}
