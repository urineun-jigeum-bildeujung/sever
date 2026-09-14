package com.golajugaenyang.product.domain.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockMovementType {
    RESERVE(null),
    CONFIRM(RESERVE),
    RELEASE(RESERVE),
    RESTORE(CONFIRM);

    private final StockMovementType requiredPrecedingType;

    public boolean requiresPrecedingMovement() {
        return requiredPrecedingType != null;
    }
}
