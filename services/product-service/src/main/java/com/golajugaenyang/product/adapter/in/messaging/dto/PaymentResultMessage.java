package com.golajugaenyang.product.adapter.in.messaging.dto;

import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import java.util.Objects;

public record PaymentResultMessage(
    Long orderItemId,
    StockSubjectType subjectType,
    Long subjectId,
    int quantity
) {

    public PaymentResultMessage {
        Objects.requireNonNull(orderItemId, "orderItemId는 필수입니다.");
        Objects.requireNonNull(subjectType, "subjectType은 필수입니다.");
        Objects.requireNonNull(subjectId, "subjectId는 필수입니다.");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다. quantity=" + quantity);
        }
    }
}
