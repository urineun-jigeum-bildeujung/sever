package com.golajugaenyang.product.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchCommand;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchResult;
import com.golajugaenyang.product.application.product.port.out.ProductSearchQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.PageCursor;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import com.golajugaenyang.product.application.product.port.out.dto.ProductSearchContext;
import com.golajugaenyang.product.domain.product.ProductSortType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ProductSearchServiceTest {

    @Mock
    private ProductSearchQueryRepository productSearchQueryRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    @Test
    @DisplayName("첫 페이지 요청에는 totalCount를 함께 조회해 반환한다.")
    void includes_total_count_when_requesting_first_page() {
        ProductSearchCommand command = new ProductSearchCommand(
            "키워드", null, ProductSortType.POPULAR, null, 20);
        when(productSearchQueryRepository.search(any())).thenReturn(List.of(projection(1L)));
        when(productSearchQueryRepository.count(any())).thenReturn(5L);

        ProductSearchResult result = productSearchService.searchProducts(command);

        assertThat(result.totalCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("다음 페이지 요청에는 totalCount 조회를 생략하고 null을 반환한다.")
    void omits_total_count_when_requesting_next_page() {
        ProductSearchContext context = new ProductSearchContext(
            ProductSortType.POPULAR, "키워드", null);
        String cursor = PageCursor.issue(context, "100", 1L).encode();
        ProductSearchCommand command = new ProductSearchCommand(
            "키워드", null, ProductSortType.POPULAR, cursor, 20);

        when(productSearchQueryRepository.search(any())).thenReturn(List.of(projection(2L)));
        ProductSearchResult result = productSearchService.searchProducts(command);

        assertThat(result.totalCount()).isNull();
        verify(productSearchQueryRepository, never()).count(any());
    }

    private ProductListProjection projection(Long id) {
        return new ProductListProjection(
            id, "https://cdn.example.com/" + id + ".jpg", "상품" + id,
            BigDecimal.valueOf(10000), BigDecimal.valueOf(12000),
            BigDecimal.valueOf(500), QuantityUnit.G,
            BigDecimal.valueOf(4.5), 10, 100);
    }
}
