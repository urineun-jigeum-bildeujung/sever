package com.golajugaenyang.product.application.timedeal.port.in.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record TimeDealListResult(
    List<TimeDealGroup> deals,
    OffsetDateTime serverTime
) {

}
