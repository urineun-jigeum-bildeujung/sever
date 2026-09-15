package com.golajugaenyang.product.application.timedeal;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetAgeGroup;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.product.port.in.ProductDetailUseCase;
import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;
import com.golajugaenyang.product.application.timedeal.port.out.TimeDealPricingRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealPricingProjection;
import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TimeDealDetailServiceTest {

    @Mock
    private TimeDealPricingRepository timeDealPricingRepository;
    @Mock
    private ProductDetailUseCase productDetailUseCase;

    @InjectMocks
    private TimeDealDetailService timeDealDetailService;

    @Test
    @DisplayName("존재하지 않는 타임딜 아이템이면 TIME_DEAL_ITEM_NOT_FOUND 예외가 발생한다.")
    void throws_when_time_deal_item_does_not_exist() {
        when(timeDealPricingRepository.findByTimeDealItemId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeDealDetailService.getTimeDealDetail(1L))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.TIME_DEAL_ITEM_NOT_FOUND);
    }

    @Test
    @DisplayName("딜이 ENDED 상태면 TIME_DEAL_ITEM_NOT_FOUND 예외가 발생한다.")
    void throws_when_deal_is_not_listable() {
        when(timeDealPricingRepository.findByTimeDealItemId(1L))
            .thenReturn(
                Optional.of(pricing(100, 0, 0, TimeDealItemStatus.ACTIVE, TimeDealStatus.ENDED)));

        assertThatThrownBy(() -> timeDealDetailService.getTimeDealDetail(1L))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.TIME_DEAL_ITEM_NOT_FOUND);
    }

    @Test
    @DisplayName("가격 관련 필드는 타임딜 값으로, 나머지는 기본 상세 조회 값을 그대로 사용한다.")
    void merges_time_deal_pricing_with_base_product_detail() {
        when(timeDealPricingRepository.findByTimeDealItemId(1L))
            .thenReturn(Optional.of(
                pricing(100, 10, 10, TimeDealItemStatus.ACTIVE, TimeDealStatus.ACTIVE)));
        when(productDetailUseCase.getProductDetail(999L)).thenReturn(baseDetail());

        ProductDetailResult result = timeDealDetailService.getTimeDealDetail(1L);

        assertThat(result.price()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        assertThat(result.originalPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));
        assertThat(result.productName()).isEqualTo("상품명");
        assertThat(result.soldOut()).isFalse();
    }

    @Test
    @DisplayName("타임딜 잔여 수량이 0이면 soldOut이 true다.")
    void marks_sold_out_when_remaining_quantity_is_zero() {
        when(timeDealPricingRepository.findByTimeDealItemId(1L))
            .thenReturn(
                Optional.of(pricing(10, 5, 5, TimeDealItemStatus.ACTIVE, TimeDealStatus.ACTIVE)));
        when(productDetailUseCase.getProductDetail(999L)).thenReturn(baseDetail());

        ProductDetailResult result = timeDealDetailService.getTimeDealDetail(1L);

        assertThat(result.soldOut()).isTrue();
    }

    private TimeDealPricingProjection pricing(
        int limit, int reserved, int sold,
        TimeDealItemStatus itemStatus, TimeDealStatus dealStatus
    ) {
        return new TimeDealPricingProjection(
            1L, 10L, 999L,
            BigDecimal.valueOf(20000), BigDecimal.valueOf(15000), BigDecimal.valueOf(25),
            limit, reserved, sold, itemStatus, dealStatus);
    }

    private ProductDetailResult baseDetail() {
        return new ProductDetailResult(
            999L, List.of("https://cdn.example.com/1.jpg"), "상품명",
            BigDecimal.valueOf(30000), BigDecimal.valueOf(30000), BigDecimal.ZERO,
            BigDecimal.valueOf(4.5), 10, false,
            "제조사", "브랜드", "한국", BigDecimal.valueOf(2), QuantityUnit.KG,
            Set.of(), "성견", TargetBreedSize.SMALL, TargetAgeGroup.ADULT, Set.of(Species.DOG),
            "1일 2회", Set.of(), Set.of(),
            "12개월", 30, "서늘한 곳");
    }
}
