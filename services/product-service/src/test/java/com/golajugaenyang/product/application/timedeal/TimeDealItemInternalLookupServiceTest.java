package com.golajugaenyang.product.application.timedeal;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealItemAvailability;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealItemInternalLookupResult;
import com.golajugaenyang.product.application.timedeal.port.out.TimeDealItemBulkQueryRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealItemInternalProjection;
import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
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
public class TimeDealItemInternalLookupServiceTest {

    @Mock
    private TimeDealItemBulkQueryRepository timeDealItemBulkQueryRepository;

    @InjectMocks
    private TimeDealItemInternalLookupService timeDealItemInternalLookupService;

    @Test
    @DisplayName("딜과 아이템이 모두 ACTIVE이고 잔여 수량이 있으면 purchasable이 true다.")
    void marks_purchasable_true_when_active_and_remaining_quantity_exists() {
        when(timeDealItemBulkQueryRepository.findAllByIds(List.of(1L))).thenReturn(
            List.of(projection(TimeDealItemStatus.ACTIVE, TimeDealStatus.ACTIVE, 10, 3, 2)));

        TimeDealItemInternalLookupResult result =
            timeDealItemInternalLookupService.getTimeDealItems(List.of(1L));

        assertThat(result.items().getFirst().purchasable()).isTrue();
        assertThat(result.items().getFirst().remainingQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("딜은 ACTIVE지만 아이템 재고가 소진되면 OUT_OF_STOCK으로 계산된다.")
    void marks_out_of_stock_when_deal_is_active_but_item_is_sold_out() {
        when(timeDealItemBulkQueryRepository.findAllByIds(List.of(1L))).thenReturn(
            List.of(projection(TimeDealItemStatus.ACTIVE, TimeDealStatus.ACTIVE, 10, 5, 5)));

        var result = timeDealItemInternalLookupService.getTimeDealItems(List.of(1L));

        assertThat(result.items().getFirst().purchasable()).isFalse();
        assertThat(result.items().getFirst().availability())
            .isEqualTo(TimeDealItemAvailability.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("딜이 ENDED면 아이템 상태와 무관하게 DEAL_ENDED로 계산된다.")
    void marks_deal_ended_when_deal_status_is_ended() {
        when(timeDealItemBulkQueryRepository.findAllByIds(List.of(1L))).thenReturn(
            List.of(projection(TimeDealItemStatus.ACTIVE, TimeDealStatus.ENDED, 10, 0, 0)));

        var result = timeDealItemInternalLookupService.getTimeDealItems(List.of(1L));

        assertThat(result.items().getFirst().purchasable()).isFalse();
        assertThat(result.items().getFirst().availability())
            .isEqualTo(TimeDealItemAvailability.DEAL_ENDED);
    }

    @Test
    @DisplayName("요청한 id 중 존재하지 않는 id는 missingTimeDealItemIds에 포함된다.")
    void includes_not_found_ids_in_missing_time_deal_item_ids() {
        when(timeDealItemBulkQueryRepository.findAllByIds(List.of(1L, 999L))).thenReturn(
            List.of(projection(TimeDealItemStatus.ACTIVE, TimeDealStatus.ACTIVE, 10, 0, 0)));

        TimeDealItemInternalLookupResult result =
            timeDealItemInternalLookupService.getTimeDealItems(List.of(1L, 999L));

        assertThat(result.items()).hasSize(1);
        assertThat(result.missingTimeDealItemIds()).containsExactly(999L);
    }

    private TimeDealItemInternalProjection projection(
        TimeDealItemStatus itemStatus, TimeDealStatus dealStatus,
        int limit, int reserved, int sold
    ) {
        return new TimeDealItemInternalProjection(
            1L, 10L, 100L, 200L,
            "https://cdn.example.com/1.jpg", "타임딜 상품",
            CategoryCode.FOOD, true,
            BigDecimal.valueOf(20000), BigDecimal.valueOf(15000), BigDecimal.valueOf(25),
            BigDecimal.valueOf(500), QuantityUnit.G,
            QuantityDimension.MASS, BigDecimal.valueOf(500), QuantityUnit.G,
            limit, reserved, sold, 2,
            itemStatus, dealStatus, OffsetDateTime.now().plusHours(1)
        );
    }
}
