package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.error.ProductErrorCode;
import com.golajugaenyang.product.support.CursorCodec;
import java.math.BigDecimal;

public record PageCursor(
    String contextFingerprint,
    String sortValue,
    Long id
) {

    private static final int SEGMENT_COUNT = 3;

    public static PageCursor decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return null;
        }
        try {
            String[] s = CursorCodec.decode(encoded, SEGMENT_COUNT);
            return new PageCursor(s[0], s[1], Long.parseLong(s[2]));
        } catch (IllegalArgumentException e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    public static PageCursor issue(CursorContext context, String sortValue, Long id) {
        return new PageCursor(context.fingerprint(), sortValue, id);
    }

    public String encode() {
        return CursorCodec.encode(contextFingerprint, sortValue, String.valueOf(id));
    }

    public void validate(CursorContext requestedContext) {
        if (!this.contextFingerprint.equals(requestedContext.fingerprint())) {
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
