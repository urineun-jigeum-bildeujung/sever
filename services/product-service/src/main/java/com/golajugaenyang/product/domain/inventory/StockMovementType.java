package com.golajugaenyang.product.domain.inventory;

import lombok.Getter;

@Getter
public enum StockMovementType {
    RESERVE,
    CONFIRM,
    RELEASE,
    RESTORE
}
