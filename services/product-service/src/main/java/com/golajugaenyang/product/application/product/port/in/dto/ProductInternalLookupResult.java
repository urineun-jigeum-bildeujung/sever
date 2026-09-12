package com.golajugaenyang.product.application.product.port.in.dto;

import java.util.List;

public record ProductInternalLookupResult(
    List<ProductInternalItem> items,
    List<Long> missingProductIds
) {

}
