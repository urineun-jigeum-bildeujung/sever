package com.golajugaenyang.product.application.timedeal;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.timedeal.port.in.TimeDealListUseCase;
import com.golajugaenyang.product.application.timedeal.port.in.dto.StockBadge;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealGroup;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealListItem;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealListResult;
import com.golajugaenyang.product.application.timedeal.port.out.TimeDealListQueryRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealListRowProjection;
import com.golajugaenyang.product.config.TimeDealListProperties;
import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TimeDealListService implements TimeDealListUseCase {

    private final TimeDealListQueryRepository timeDealListQueryRepository;
    private final TimeDealListProperties timeDealListProperties;

    private static final Set<TimeDealStatus> LISTABLE_STATUSES = EnumSet.of(
        TimeDealStatus.ACTIVE, TimeDealStatus.SCHEDULED);

    @Override
    @Transactional(readOnly = true)
    public TimeDealListResult getTimeDeals(TimeDealStatus status) {
        if (!LISTABLE_STATUSES.contains(status)) {
            throw new AppException(ProductErrorCode.INVALID_TIME_DEAL_STATUS);
        }

        List<TimeDealListRowProjection> rows =
            timeDealListQueryRepository.findItemsByDealStatus(status);

        LinkedHashMap<Long, List<TimeDealListRowProjection>> byDeal = rows.stream()
            .collect(Collectors.groupingBy(
                TimeDealListRowProjection::dealId,
                LinkedHashMap::new, Collectors.toList()));

        List<TimeDealGroup> groups = byDeal.values().stream()
            .map(this::toGroup)
            .toList();

        return new TimeDealListResult(groups, OffsetDateTime.now());
    }

    private TimeDealGroup toGroup(List<TimeDealListRowProjection> rows) {
        TimeDealListRowProjection first = rows.getFirst();
        List<TimeDealListItem> items = rows.stream().map(this::toItem).toList();
        return new TimeDealGroup(
            first.dealId(), first.dealName(), first.dealStartAt(), first.dealEndAt(), items);
    }

    private TimeDealListItem toItem(TimeDealListRowProjection row) {
        int remaining = Math.max(
            row.quantityLimit() - row.reservedQuantity() - row.soldQuantity(), 0);
        StockBadge badge = resolveStockBadge(row.itemStatus(), remaining, row.quantityLimit());

        return new TimeDealListItem(
            row.productId(), row.timeDealItemId(), row.thumbnailUrl(), row.productName(),
            row.normalPrice(), row.discountedPrice(), row.discountRate(),
            null, // TODO: dailyPrice는 데이터 소스 확정 전까지 null
            badge
        );
    }

    private StockBadge resolveStockBadge(
        TimeDealItemStatus itemStatus,
        int remaining, int quantityLimit
    ) {
        if (itemStatus == TimeDealItemStatus.SOLD_OUT || remaining <= 0) {
            return StockBadge.SOLD_OUT;
        }
        if (quantityLimit > 0) {
            double remainingRatio = (double) remaining / quantityLimit;
            if (remainingRatio <= timeDealListProperties.lowStockThresholdRatio()) {
                return StockBadge.LOW_STOCK;
            }
        }
        return StockBadge.NONE;
    }
}
