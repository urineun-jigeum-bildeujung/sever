package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.domain.product.ProductSortType;
import com.golajugaenyang.product.error.ProductErrorCode;
import com.golajugaenyang.product.support.CursorCodec;
import java.math.BigDecimal;


public record ProductCursor(
    ProductSortType sortType,
    String sortValue,
    Long id
) {

    private static final int SEGMENT_COUNT = 3;

    public static ProductCursor decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return null;
        }
        try {
            String[] segments = CursorCodec.decode(encoded, SEGMENT_COUNT);
            ProductSortType sortType = ProductSortType.valueOf(segments[0]);
            String sortValue = segments[1];
            Long id = Long.parseLong(segments[2]);
            return new ProductCursor(sortType, sortValue, id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    public String encode() {
        return CursorCodec.encode(sortType.name(), sortValue, String.valueOf(id));
    }

    public void validateSortType(ProductSortType effectiveSort) {
        if (this.sortType != effectiveSort) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    public int sortValueAsInt() {
        try {
            return Integer.parseInt(sortValue);
        } catch (NumberFormatException e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    public BigDecimal sortValueAsBigDecimal() {
        try {
            return new BigDecimal(sortValue);
        } catch (NumberFormatException e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }
}
