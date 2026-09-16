package com.golajugaenyang.order.adapter.out.external.product.dto;

import java.util.List;


public record ProductInternalItemsResponse(
    List<ProductInternalItemResponse> items,
    List<Long> missingProductIds
) {

}
