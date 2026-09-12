package com.golajugaenyang.product.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetAgeGroup;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;
import com.golajugaenyang.product.application.product.port.out.ProductDetailQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.ProductDetailProjection;
import com.golajugaenyang.product.domain.product.ProductStatus;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductDetailServiceTest {

    @Mock
    private ProductDetailQueryRepository productDetailQueryRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    @Test
    @DisplayName("존재하지 않는 상품 ID를 조회하면 PRODUCT_NOT_FOUND 예외가 발생한다.")
    void throws_product_not_found_when_product_does_not_exist() {
        when(productDetailQueryRepository.findDetailById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productDetailService.getProductDetail(999L))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하는 상품 ID를 조회하면 상세 정보를 정상 반환한다.")
    void returns_detail_when_product_exists() {
        when(productDetailQueryRepository.findDetailById(1L))
            .thenReturn(Optional.of(sampleProjection()));

        ProductDetailResult result = productDetailService.getProductDetail(1L);

        assertThat(result.productId()).isEqualTo(1L);
    }

    private ProductDetailProjection sampleProjection() {
        return new ProductDetailProjection(
            1L, "https://cdn.example.com/thumb.jpg", java.util.List.of(), "상품명",
            java.math.BigDecimal.valueOf(10000), java.math.BigDecimal.valueOf(12000),
            java.math.BigDecimal.valueOf(4.5), 10,
            ProductStatus.ON_SALE,
            "제조사", "브랜드", "한국",
            java.math.BigDecimal.valueOf(2), QuantityUnit.KG,
            java.util.Set.of(), "성견",
            TargetBreedSize.SMALL,
            TargetAgeGroup.ADULT,
            java.util.Set.of(Species.DOG),
            "1일 2회", java.util.Set.of(), java.util.Set.of(),
            "12개월", 30, "서늘한 곳"
        );
    }
}
