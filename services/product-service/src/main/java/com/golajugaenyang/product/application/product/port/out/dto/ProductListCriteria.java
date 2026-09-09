package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListCommand;
import com.golajugaenyang.product.domain.product.ProductSortType;

public record ProductListCriteria(
    CategoryCode category,
    ProductSortType sortType,
    ProductCursor cursor,
    int size
) {

    public static ProductListCriteria of(ProductListCommand command, ProductCursor cursor) {
        return new ProductListCriteria(
            command.category(),
            command.sortType(),
            cursor,
            command.size()
        );
    }
}
