package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public record ProductCursor(
    String sortValue,
    Long id
) {

    public static ProductCursor decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return null;
        }
        try {
            String decoded = new String(
                Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
            int idx = decoded.lastIndexOf('_');
            String sortValue = decoded.substring(0, idx);
            Long id = Long.parseLong(decoded.substring(idx + 1));
            return new ProductCursor(sortValue, id);
        } catch (Exception e) {
            throw new AppException(ProductErrorCode.INVALID_CURSOR);
        }
    }

    public String encode() {
        String raw = sortValue + "_" + id;
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
