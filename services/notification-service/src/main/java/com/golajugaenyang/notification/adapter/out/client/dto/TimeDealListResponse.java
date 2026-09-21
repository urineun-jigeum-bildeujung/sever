package com.golajugaenyang.notification.adapter.out.client.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record TimeDealListResponse(
        List<DealGroupResponse> deals,
        OffsetDateTime serverTime
) {

    public record DealGroupResponse(
            Long dealId,
            String dealName,
            OffsetDateTime startAt,
            OffsetDateTime endAt
    ) {

    }
}
