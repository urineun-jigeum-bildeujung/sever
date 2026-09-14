package com.golajugaenyang.product.adapter.in.web.timedeal.dto;

import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealListResult;
import java.math.BigDecimal;
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
        OffsetDateTime endAt,
        List<ItemResponse> items
    ) {

    }

    public record ItemResponse(
        Long productId,
        Long timeDealItemId,
        String thumbnailUrl,
        String productName,
        BigDecimal normalPrice,
        BigDecimal discountedPrice,
        BigDecimal discountRate,
        BigDecimal dailyPrice,
        String stockBadge
    ) {

    }

    public static TimeDealListResponse from(TimeDealListResult result) {
        List<DealGroupResponse> deals = result.deals().stream()
            .map(g -> new DealGroupResponse(
                g.dealId(), g.dealName(), g.startAt(), g.endAt(),
                g.items().stream()
                    .map(i -> new ItemResponse(
                        i.productId(), i.timeDealItemId(), i.thumbnailUrl(), i.productName(),
                        i.normalPrice(), i.discountedPrice(), i.discountRate(),
                        i.dailyPrice(), i.stockBadge().name()))
                    .toList()))
            .toList();
        return new TimeDealListResponse(deals, result.serverTime());
    }
}
