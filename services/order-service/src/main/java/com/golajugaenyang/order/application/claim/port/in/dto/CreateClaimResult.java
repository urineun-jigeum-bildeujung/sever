package com.golajugaenyang.order.application.claim.port.in.dto;

import com.golajugaenyang.order.domain.claim.OrderClaim;
import java.time.Instant;

public record CreateClaimResult(
    Long claimId,
    String claimType,
    String claimStatus,
    Instant requestedAt
) {

    public static CreateClaimResult from(OrderClaim claim) {
        return new CreateClaimResult(
            claim.getId(),
            claim.getClaimType().name(),
            claim.getClaimStatus().name(),
            claim.getRequestedAt());
    }
}
