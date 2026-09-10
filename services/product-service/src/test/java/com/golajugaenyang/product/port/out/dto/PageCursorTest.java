package com.golajugaenyang.product.port.out.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.product.port.out.dto.PageCursor;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListContext;
import com.golajugaenyang.product.application.product.port.out.dto.ProductSearchContext;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.golajugaenyang.product.error.ProductErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class PageCursorTest {

    @Test
    @DisplayName("issue 후 encode/decode하면 동일한 커서로 복원된다.")
    void decodes_issued_cursor_to_equal_value() {
        ProductListContext context = new ProductListContext(
            ProductSortType.POPULAR, CategoryCode.FOOD);
        PageCursor issued = PageCursor.issue(context, "500", 42L);

        assertThat(PageCursor.decode(issued.encode())).isEqualTo(issued);
    }

    @Test
    @DisplayName("동일한 컨텍스트에서 발급된 커서는 검증을 통과한다.")
    void passes_validation_when_context_matches() {
        ProductSearchContext context = new ProductSearchContext(
            ProductSortType.POPULAR, "키워드", CategoryCode.FOOD);
        PageCursor cursor = PageCursor.issue(context, "500", 10L);

        assertThatCode(() -> cursor.validate(context)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("정렬 기준이 다른 컨텍스트로 검증하면 INVALID_CURSOR 예외가 발생한다.")
    void throws_invalid_cursor_when_sort_type_differs() {
        ProductListContext issuedContext = new ProductListContext(
            ProductSortType.PRICE_DESC, CategoryCode.FOOD);
        PageCursor cursor = PageCursor.issue(issuedContext, "45000.00", 5L);

        ProductListContext requestedContext = new ProductListContext(
            ProductSortType.POPULAR, CategoryCode.FOOD);

        assertThatThrownBy(() -> cursor.validate(requestedContext))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_CURSOR);
    }

    @Test
    @DisplayName("카테고리가 다른 컨텍스트로 검증하면 INVALID_CURSOR 예외가 발생한다.")
    void throws_invalid_cursor_when_category_differs() {
        ProductListContext issuedContext = new ProductListContext(
            ProductSortType.POPULAR, CategoryCode.FOOD);
        PageCursor cursor = PageCursor.issue(issuedContext, "500", 10L);

        ProductListContext requestedContext = new ProductListContext(
            ProductSortType.POPULAR, CategoryCode.TREAT);

        assertThatThrownBy(() -> cursor.validate(requestedContext))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_CURSOR);
    }

    @Test
    @DisplayName("검색어가 다른 컨텍스트로 검증하면 INVALID_CURSOR 예외가 발생한다.")
    void throws_invalid_cursor_when_keyword_differs() {
        ProductSearchContext issuedContext = new ProductSearchContext(
            ProductSortType.POPULAR, "키워드1", null);
        PageCursor cursor = PageCursor.issue(issuedContext, "500", 10L);

        ProductSearchContext requestedContext = new ProductSearchContext(
            ProductSortType.POPULAR, "키워드2", null);

        assertThatThrownBy(() -> cursor.validate(requestedContext))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_CURSOR);
    }

    @Test
    @DisplayName("목록 조회에서 발급된 커서를 검색 컨텍스트로 검증하면 INVALID_CURSOR 예외가 발생한다.")
    void throws_invalid_cursor_when_list_cursor_is_validated_against_search_context() {
        ProductListContext listContext = new ProductListContext(
            ProductSortType.POPULAR, CategoryCode.FOOD);
        PageCursor listCursor = PageCursor.issue(listContext, "500", 10L);

        ProductSearchContext searchContext = new ProductSearchContext(
            ProductSortType.POPULAR, "키워드", CategoryCode.FOOD);

        assertThatThrownBy(() -> listCursor.validate(searchContext))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_CURSOR);
    }

    @Test
    @DisplayName("형식이 잘못된 커서 문자열은 INVALID_CURSOR 예외를 던진다.")
    void throws_invalid_cursor_for_malformed_string() {
        assertThatThrownBy(() -> PageCursor.decode("this-is-not-valid!!"))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.INVALID_CURSOR);
    }
}
