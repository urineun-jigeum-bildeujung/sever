package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.product.domain.product.ProductSortType;
import java.math.BigDecimal;

public sealed interface SortCursor permits ProductCursor, ProductSearchCursor {

    ProductSortType sortType();

    Long id();

    int sortValueAsInt();

    BigDecimal sortValueAsBigDecimal();
}
