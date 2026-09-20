package com.golajugaenyang.order.adapter.in.web.order;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderListCursorCodec {

    public record Cursor(
        OffsetDateTime orderedAt,
        Long orderId
    ) {

    }

    public static Cursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            String raw = new String(
                Base64.getUrlDecoder().decode(cursor),
                StandardCharsets.UTF_8);
            String[] parts = raw.split(":::", 2);
            return new Cursor(OffsetDateTime.parse(parts[0]), Long.parseLong(parts[1]));
        } catch (Exception e) {
            throw new AppException(OrderErrorCode.INVALID_CURSOR);
        }
    }

    public static String encode(OffsetDateTime orderedAt, Long orderId) {
        String raw = orderedAt.toString() + ":::" + orderId;
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
