package com.golajugaenyang.product.application.product;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.product.application.product.port.in.dto.ProductAvailability;
import com.golajugaenyang.product.application.product.port.in.dto.ProductInternalLookupResult;
import com.golajugaenyang.product.application.product.port.out.ProductBulkQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.ProductInternalProjection;
import com.golajugaenyang.product.domain.product.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ProductInternalLookupServiceTest {

    @Mock
    private ProductBulkQueryRepository productBulkQueryRepository;

    @InjectMocks
    private ProductInternalLookupService productInternalLookupService;

    @Test
    @DisplayName("요청 id 목록이 비어있으면 DB를 호출하지 않고 빈 결과를 반환한다.")
    void returns_empty_result_without_querying_when_ids_is_empty() {
        ProductInternalLookupResult result =
            productInternalLookupService.getProducts(List.of());

        assertThat(result.items()).isEmpty();
        assertThat(result.missingProductIds()).isEmpty();
        verify(productBulkQueryRepository, never()).findAllByIds(any());
    }

    @Test
    @DisplayName("SOLD_OUT 상태의 상품은 OUT_OF_STOCK으로 계산된다.")
    void marks_out_of_stock_when_status_is_sold_out() {
        when(productBulkQueryRepository.findAllByIds(List.of(1L)))
            .thenReturn(List.of(projection(1L, ProductStatus.SOLD_OUT)));

        ProductInternalLookupResult result =
            productInternalLookupService.getProducts(List.of(1L));

        assertThat(result.items().getFirst().availability())
            .isEqualTo(ProductAvailability.OUT_OF_STOCK);
        assertThat(result.items().getFirst().purchasable()).isFalse();
    }

    @Test
    @DisplayName("DISCONTINUED 상태의 상품은 DISCONTINUED로 계산된다.")
    void distinguishes_discontinued_from_sold_out() {
        when(productBulkQueryRepository.findAllByIds(List.of(1L)))
            .thenReturn(List.of(projection(1L, ProductStatus.DISCONTINUED)));

        ProductInternalLookupResult result =
            productInternalLookupService.getProducts(List.of(1L));

        assertThat(result.items().getFirst().availability())
            .isEqualTo(ProductAvailability.DISCONTINUED);
        assertThat(result.items().getFirst().purchasable()).isFalse();
    }

    @Test
    @DisplayName("요청한 id 중 존재하지 않는 id는 missingProductIds에 포함된다.")
    void includes_not_found_ids_in_missing_product_ids() {
        when(productBulkQueryRepository.findAllByIds(List.of(1L, 999L)))
            .thenReturn(List.of(projection(1L, ProductStatus.ON_SALE)));

        ProductInternalLookupResult result =
            productInternalLookupService.getProducts(List.of(1L, 999L));

        assertThat(result.items()).hasSize(1);
        assertThat(result.missingProductIds()).containsExactly(999L);
    }

    @Test
    @DisplayName("요청한 id에 중복이 있어도 missingProductIds에는 중복 없이 한 번만 포함된다.")
    void deduplicates_missing_product_ids() {
        when(productBulkQueryRepository.findAllByIds(List.of(999L, 999L)))
            .thenReturn(List.of());

        ProductInternalLookupResult result =
            productInternalLookupService.getProducts(List.of(999L, 999L));

        assertThat(result.missingProductIds()).containsExactly(999L);
    }

    private ProductInternalProjection projection(Long id, ProductStatus status) {
        return new ProductInternalProjection(
            id, 100L, "https://cdn.example.com/1.jpg", "상품명",
            CategoryCode.FOOD, true,
            BigDecimal.valueOf(10000), BigDecimal.valueOf(12000),
            BigDecimal.valueOf(2), QuantityUnit.KG,
            QuantityDimension.MASS, BigDecimal.valueOf(2000), QuantityUnit.G,
            status
        );
    }
}
