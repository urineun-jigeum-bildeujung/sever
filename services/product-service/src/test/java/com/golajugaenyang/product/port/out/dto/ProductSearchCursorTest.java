package com.golajugaenyang.product.port.out.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.product.port.out.dto.ProductSearchCursor;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.golajugaenyang.product.error.ProductErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductSearchCursorTest {

    @Test
    @DisplayName("encode 후 decode하면 원래 값을 그대로 복원한다.")
    void decodes_encoded_cursor_to_original_values() {
        ProductSearchCursor original = new ProductSearchCursor(
            ProductSortType.POPULAR, "키워드", "500", 42L);

        assertThat(ProductSearchCursor.decode(original.encode())).isEqualTo(original);
    }

    @Test
    @DisplayName("커서의 검색어와 요청 검색어가 다르면 INVALID_CURSOR 예외를 던진다.")
    void throws_invalid_cursor_when_keyword_does_not_match() {
        ProductSearchCursor cursor = new ProductSearchCursor(
            ProductSortType.POPULAR, "키워드", "500", 42L);

        assertThatThrownBy(() -> cursor.validate(ProductSortType.POPULAR, "다른키워드"))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_CURSOR);
    }

    @Test
    @DisplayName("커서의 정렬 기준과 요청 정렬 기준이 다르면 INVALID_CURSOR 예외를 던진다.")
    void throws_invalid_cursor_when_sort_type_does_not_match() {
        ProductSearchCursor cursor = new ProductSearchCursor(
            ProductSortType.PRICE_DESC, "키워드", "45000.00", 5L);

        assertThatThrownBy(() -> cursor.validate(ProductSortType.POPULAR, "키워드"))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_CURSOR);
    }
}
