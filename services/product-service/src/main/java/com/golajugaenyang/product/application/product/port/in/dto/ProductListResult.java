package com.golajugaenyang.product.application.product.port.in.dto;

import java.util.List;

public record ProductListResult(
    List<ProductListItem> items,
    String nextCursor,
    boolean hasNext
) {

}
