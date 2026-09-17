package com.golajugaenyang.payment.domain.payment;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;

@Getter
public enum PaymentStatus {
    READY,
    DONE,
    FAILED;

    private static final Map<PaymentStatus, Set<PaymentStatus>> ALLOWED = Map.of(
        READY, EnumSet.of(DONE, FAILED),
        DONE, EnumSet.noneOf(PaymentStatus.class),
        FAILED, EnumSet.noneOf(PaymentStatus.class)
    );

    public boolean canTransitTo(PaymentStatus next) {
        return ALLOWED.getOrDefault(this, EnumSet.noneOf(PaymentStatus.class))
            .contains(next);
    }
}
