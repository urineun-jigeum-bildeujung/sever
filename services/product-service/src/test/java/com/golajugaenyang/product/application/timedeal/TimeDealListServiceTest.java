package com.golajugaenyang.product.application.timedeal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.timedeal.port.in.dto.StockBadge;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealListResult;
import com.golajugaenyang.product.application.timedeal.port.out.TimeDealListQueryRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealListRowProjection;
import com.golajugaenyang.product.config.TimeDealListProperties;
import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class TimeDealListServiceTest {

    @Mock
    private TimeDealListQueryRepository timeDealListQueryRepository;

    @InjectMocks
    private TimeDealListService timeDealListService;

    private final TimeDealListProperties properties =
        new TimeDealListProperties(0.1);

    @Test
    @DisplayName("ACTIVE/SCHEDULED가 아닌 상태를 요청하면 예외가 발생한다.")
    void throws_when_status_is_not_active_or_scheduled() {
        TimeDealListService service =
            new TimeDealListService(timeDealListQueryRepository, properties);

        assertThatThrownBy(() -> service.getTimeDeals(TimeDealStatus.ENDED))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_TIME_DEAL_STATUS);
    }

    @Test
    @DisplayName("잔여 수량이 0이면 StockBadge가 SOLD_OUT로 적용된다.")
    void marks_sold_out_when_remaining_is_zero() {
        TimeDealListService service =
            new TimeDealListService(timeDealListQueryRepository, properties);
        when(timeDealListQueryRepository.findItemsByDealStatus(TimeDealStatus.ACTIVE))
            .thenReturn(List.of(row(10, 5, 5, TimeDealItemStatus.ACTIVE)));

        TimeDealListResult result = service.getTimeDeals(TimeDealStatus.ACTIVE);

        assertThat(result.deals().getFirst().items().getFirst().stockBadge()).isEqualTo(
            StockBadge.SOLD_OUT);
    }

    @Test
    @DisplayName("잔여 비율이 임계값 이하이면 StockBadge가 LOW_STOCK로 적용된다.")
    void marks_low_stock_when_remaining_ratio_is_below_threshold() {
        TimeDealListService service =
            new TimeDealListService(timeDealListQueryRepository, properties);
        when(timeDealListQueryRepository.findItemsByDealStatus(TimeDealStatus.ACTIVE))
            .thenReturn(List.of(row(100, 90, 5, TimeDealItemStatus.ACTIVE)));

        TimeDealListResult result = service.getTimeDeals(TimeDealStatus.ACTIVE);

        assertThat(result.deals().getFirst().items().getFirst().stockBadge()).isEqualTo(
            StockBadge.LOW_STOCK);
    }

    @Test
    @DisplayName("잔여 비율이 임계값보다 크면 StockBadge이 NONE이다.")
    void marks_none_when_remaining_ratio_is_above_threshold() {
        TimeDealListService service =
            new TimeDealListService(timeDealListQueryRepository, properties);
        when(timeDealListQueryRepository.findItemsByDealStatus(TimeDealStatus.ACTIVE))
            .thenReturn(List.of(row(100, 20, 0, TimeDealItemStatus.ACTIVE)));

        TimeDealListResult result = service.getTimeDeals(TimeDealStatus.ACTIVE);

        assertThat(result.deals().getFirst().items().getFirst().stockBadge())
            .isEqualTo(StockBadge.NONE);
    }

    @Test
    @DisplayName("같은 dealId의 여러 아이템은 하나의 딜 그룹으로 묶인다.")
    void groups_items_by_deal_id() {
        TimeDealListService service =
            new TimeDealListService(timeDealListQueryRepository, properties);
        when(timeDealListQueryRepository.findItemsByDealStatus(TimeDealStatus.ACTIVE))
            .thenReturn(List.of(
                row(10, 0, 0, TimeDealItemStatus.ACTIVE),
                row(10, 0, 0, TimeDealItemStatus.ACTIVE)));

        TimeDealListResult result = service.getTimeDeals(TimeDealStatus.ACTIVE);

        assertThat(result.deals()).hasSize(1);
        assertThat(result.deals().getFirst().items()).hasSize(2);
    }

    private TimeDealListRowProjection row(
        int limit, int reserved, int sold, TimeDealItemStatus itemStatus) {
        return new TimeDealListRowProjection(
            1L, "딜 이름", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1),
            1L, 100L, "https://cdn.example.com/1.jpg", "상품명",
            BigDecimal.valueOf(20000), BigDecimal.valueOf(15000), BigDecimal.valueOf(25),
            limit, reserved, sold, itemStatus);
    }
}
