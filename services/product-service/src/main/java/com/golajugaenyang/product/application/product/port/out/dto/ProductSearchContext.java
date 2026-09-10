package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.domain.product.ProductSortType;

public record ProductSearchContext(
    ProductSortType sortType,
    String keyword,
    CategoryCode category

) implements CursorContext {

    @Override
    public String[] fingerprintSegments() {
        return new String[]{
            sortType.name(),
            keyword,
            category == null ? "" : category.name()
        };
    }
}
