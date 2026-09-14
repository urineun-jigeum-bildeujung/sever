package com.golajugaenyang.product.application.timedeal.port.in.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record TimeDealGroup(
    Long dealId,
    String dealName,
    OffsetDateTime startAt,
    OffsetDateTime endAt,
    List<TimeDealListItem> items
) {

}
