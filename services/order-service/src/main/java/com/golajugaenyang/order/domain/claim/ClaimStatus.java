package com.golajugaenyang.order.domain.claim;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum ClaimStatus {
    REQUESTED,
    COLLECTING,
    INSPECTING,
    COMPLETED,
    REJECTED;

    private static final Map<ClaimStatus, Set<ClaimStatus>> ALLOWED = Map.of(
        REQUESTED, EnumSet.of(COLLECTING, REJECTED),
        COLLECTING, EnumSet.of(INSPECTING, REJECTED),
        INSPECTING, EnumSet.of(COMPLETED, REJECTED),
        COMPLETED, EnumSet.noneOf(ClaimStatus.class),
        REJECTED, EnumSet.noneOf(ClaimStatus.class)
    );

    public boolean canTransitTo(ClaimStatus next) {
        return ALLOWED.getOrDefault(this, EnumSet.noneOf(ClaimStatus.class)).contains(next);
    }

    public static Set<ClaimStatus> terminalStates() {
        return Arrays.stream(values())
            .filter(s ->
                ALLOWED.getOrDefault(s, EnumSet.noneOf(ClaimStatus.class)).isEmpty())
            .collect(Collectors.toSet());
    }
}
