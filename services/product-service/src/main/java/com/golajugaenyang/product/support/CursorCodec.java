package com.golajugaenyang.product.support;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import lombok.experimental.UtilityClass;


@UtilityClass
public class CursorCodec {

    private final String DELIMITER = ":::";
    private final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder DECODER = Base64.getUrlDecoder();

    public String encode(String... segments) {
        return Arrays.stream(segments)
            .map(CursorCodec::encodeSegment)
            .reduce((a, b) -> a + DELIMITER + b)
            .orElse("");
    }

    /**
     * @throws IllegalArgumentException Base64 디코딩 실패 또는 세그먼트 개수 불일치 시
     */
    public String[] decode(String cursor, int expectedSegmentCount) {
        String[] parts = cursor.split(DELIMITER, -1);
        if (parts.length != expectedSegmentCount) {
            throw new IllegalArgumentException(
                "커서 세그먼트 개수가 올바르지 않습니다. "
                    + "expected=" + expectedSegmentCount + ", actual=" + parts.length);
        }
        return Arrays.stream(parts)
            .map(CursorCodec::decodeSegment)
            .toArray(String[]::new);
    }

    private String encodeSegment(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeSegment(String value) {
        return new String(DECODER.decode(value), StandardCharsets.UTF_8);
    }
}
