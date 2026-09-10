package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.domain.product.ProductSortType;

public record ProductListContext(
    ProductSortType sortType,
    CategoryCode category

) implements CursorContext {

    @Override
    public String[] fingerprintSegments() {
        return new String[]{
            sortType.name(),
            category == null ? "" : category.name()
        };
    }
}
