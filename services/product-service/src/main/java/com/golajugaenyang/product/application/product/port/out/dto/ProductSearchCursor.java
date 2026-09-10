package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.golajugaenyang.product.error.ProductErrorCode;
import com.golajugaenyang.product.support.CursorCodec;
import java.math.BigDecimal;

public record ProductSearchCursor(
    ProductSortType sortType,
    String keyword,
    String sortValue,
    Long id
) implements SortCursor {

    private static final int SEGMENT_COUNT = 4;

    public static ProductSearchCursor decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return null;
        }
        try {
            String[] s = CursorCodec.decode(encoded, SEGMENT_COUNT);
            return new ProductSearchCursor(
                ProductSortType.valueOf(s[0]), s[1], s[2], Long.parseLong(s[3]));
        } catch (IllegalArgumentException e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    public String encode() {
        return CursorCodec.encode(sortType.name(), keyword, sortValue, String.valueOf(id));
    }

    public void validate(ProductSortType effectiveSort, String requestedKeyword) {
        if (this.sortType != effectiveSort || !this.keyword.equals(requestedKeyword)) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    @Override
    public int sortValueAsInt() {
        try {
            return Integer.parseInt(sortValue);
        } catch (NumberFormatException e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    @Override
    public BigDecimal sortValueAsBigDecimal() {
        try {
            return new BigDecimal(sortValue);
        } catch (NumberFormatException e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }
}
