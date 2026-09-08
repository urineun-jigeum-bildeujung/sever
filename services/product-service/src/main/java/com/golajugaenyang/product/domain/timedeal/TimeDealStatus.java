package com.golajugaenyang.product.domain.timedeal;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;


@Getter
public enum TimeDealStatus {
    DRAFT,
    SCHEDULED,
    ACTIVE,
    ENDED,
    CANCELLED;

    private static final Map<TimeDealStatus, Set<TimeDealStatus>> ALLOWED = Map.of(
        DRAFT, EnumSet.of(SCHEDULED, CANCELLED),
        SCHEDULED, EnumSet.of(ACTIVE, CANCELLED),
        ACTIVE, EnumSet.of(ENDED, CANCELLED),
        ENDED, EnumSet.noneOf(TimeDealStatus.class),
        CANCELLED, EnumSet.noneOf(TimeDealStatus.class)
    );

    public boolean canTransitTo(TimeDealStatus next) {
        return ALLOWED
            .getOrDefault(this, EnumSet.noneOf(TimeDealStatus.class))
            .contains(next);
    }
}
