package com.golajugaenyang.common.jpa.entity;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;


@Getter
public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED;

    private static final Map<OutboxStatus, Set<OutboxStatus>> ALLOWED = Map.of(
        PENDING, EnumSet.of(SENT, FAILED),
        FAILED, EnumSet.of(SENT, FAILED),
        SENT, EnumSet.noneOf(OutboxStatus.class)
    );

    public boolean canTransitTo(OutboxStatus next) {
        return ALLOWED
            .getOrDefault(this, EnumSet.noneOf(OutboxStatus.class))
            .contains(next);
    }
}
