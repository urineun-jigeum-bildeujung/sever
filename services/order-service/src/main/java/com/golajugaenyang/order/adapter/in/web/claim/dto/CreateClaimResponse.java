package com.golajugaenyang.order.adapter.in.web.claim.dto;

import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimResult;
import java.time.Instant;

public record CreateClaimResponse(
    Long claimId,
    String claimType,
    String claimStatus,
    Instant requestedAt
) {

    public static CreateClaimResponse from(CreateClaimResult result) {
        return new CreateClaimResponse(
            result.claimId(),
            result.claimType(),
            result.claimStatus(),
            result.requestedAt());
    }
}
