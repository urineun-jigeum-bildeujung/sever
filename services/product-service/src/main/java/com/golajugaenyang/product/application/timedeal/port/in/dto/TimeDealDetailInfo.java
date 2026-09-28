package com.golajugaenyang.product.application.timedeal.port.in.dto;

import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import java.time.OffsetDateTime;

public record TimeDealDetailInfo(
    Long timeDealItemId,
    Long dealId,
    TimeDealStatus dealStatus,
    OffsetDateTime startAt,
    OffsetDateTime endAt,
    OffsetDateTime serverTime,
    boolean purchasable
) {

}
