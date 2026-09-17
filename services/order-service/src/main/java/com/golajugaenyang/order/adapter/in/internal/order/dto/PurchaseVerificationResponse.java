package com.golajugaenyang.order.adapter.in.internal.order.dto;

import java.util.List;

public record PurchaseVerificationResponse(
    List<PurchaseVerificationItemResponse> items
) {

}
