package com.golajugaenyang.product.application.product.port.in.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.domain.product.ProductSortType;

public record ProductSearchCommand(
    String keyword,
    CategoryCode category,
    ProductSortType sortType,
    String cursor,
    int size
) {

}
