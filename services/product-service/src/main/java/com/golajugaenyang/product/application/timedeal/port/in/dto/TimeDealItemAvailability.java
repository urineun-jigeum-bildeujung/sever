package com.golajugaenyang.product.application.timedeal.port.in.dto;

import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;

public enum TimeDealItemAvailability {
    AVAILABLE,
    OUT_OF_STOCK,
    DEAL_ENDED;

    public static TimeDealItemAvailability from(
        TimeDealStatus dealStatus,
        TimeDealItemStatus itemStatus,
        int remainingQuantity
    ) {
        if (dealStatus != TimeDealStatus.ACTIVE) {
            return DEAL_ENDED;
        }
        if (itemStatus != TimeDealItemStatus.ACTIVE || remainingQuantity <= 0) {
            return OUT_OF_STOCK;
        }
        return AVAILABLE;
    }
}