package com.golajugaenyang.payment.domain.payment;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;

@Getter
public enum PaymentStatus {
    READY,
    APPROVED,
    DONE,
    FAILED,
    CANCELLED;

    private static final Map<PaymentStatus, Set<PaymentStatus>> ALLOWED = Map.of(
        READY, EnumSet.of(APPROVED, FAILED),
        APPROVED, EnumSet.of(DONE),
        DONE, EnumSet.of(CANCELLED),
        FAILED, EnumSet.noneOf(PaymentStatus.class),
        CANCELLED, EnumSet.noneOf(PaymentStatus.class)
    );

    public boolean canTransitTo(PaymentStatus next) {
        return ALLOWED.getOrDefault(this, EnumSet.noneOf(PaymentStatus.class))
            .contains(next);
    }
}
