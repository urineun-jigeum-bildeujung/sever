package com.golajugaenyang.order.domain.order;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;

@Getter
public enum ReservationStatus {
    REQUESTED,
    CONFIRMED,
    FAILED;

    private static final Map<ReservationStatus, Set<ReservationStatus>> ALLOWED = Map.of(
        REQUESTED, EnumSet.of(CONFIRMED, FAILED),
        CONFIRMED, EnumSet.noneOf(ReservationStatus.class),
        FAILED, EnumSet.noneOf(ReservationStatus.class)
    );

    public boolean canTransitTo(ReservationStatus next) {
        return ALLOWED.getOrDefault(
                this, EnumSet.noneOf(ReservationStatus.class))
            .contains(next);
    }
}
