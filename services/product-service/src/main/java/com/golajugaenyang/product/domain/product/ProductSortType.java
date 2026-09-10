package com.golajugaenyang.product.domain.product;

import lombok.Getter;


@Getter
public enum ProductSortType {
    RECOMMEND,
    POPULAR,
    REVIEW,
    PRICE_DESC,
    PRICE_ASC;

    public ProductSortType resolveEffectiveSort() {
        return this == RECOMMEND ? POPULAR : this;
    }
}
