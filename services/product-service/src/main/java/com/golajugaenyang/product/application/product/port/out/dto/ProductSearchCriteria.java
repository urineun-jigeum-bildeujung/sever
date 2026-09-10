package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchCommand;
import com.golajugaenyang.product.domain.product.ProductSortType;

public record ProductSearchCriteria(
    String keyword,
    CategoryCode category,
    ProductSortType effectiveSortType,
    PageCursor cursor,
    int size
) {

    public static ProductSearchCriteria of(
        ProductSearchCommand command,
        ProductSortType effectiveSortType,
        PageCursor cursor
    ) {
        return new ProductSearchCriteria(
            command.keyword(),
            command.category(),
            effectiveSortType,
            cursor,
            command.size()
        );
    }
}
