package com.golajugaenyang.product.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CursorCodecTest {

    @Test
    @DisplayName("인코딩 후 디코딩하면 원본 세그먼트를 그대로 복원한다.")
    void success_decodesEncodedSegments_BackToOriginalValues() {
        String encoded = CursorCodec.encode("POPULAR", "500", "42");

        String[] decoded = CursorCodec.decode(encoded, 3);

        assertThat(decoded).containsExactly("POPULAR", "500", "42");
    }

    @Test
    @DisplayName("세그먼트 값에 구분자와 같은 문자가 포함되어도 정상적으로 복원한다.")
    void success_handlesSegment_ValuesContainingDelimiterCharacters() {
        String encoded = CursorCodec.encode("a:::b", "value", "1");

        String[] decoded = CursorCodec.decode(encoded, 3);

        assertThat(decoded).containsExactly("a:::b", "value", "1");
    }

    @Test
    @DisplayName("기대 세그먼트 개수와 실제 개수가 다르면 예외를 던진다.")
    void throws_when_SegmentCountMismatches() {
        String encoded = CursorCodec.encode("a", "b");

        assertThatThrownBy(() -> CursorCodec.decode(encoded, 3))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Base64로 디코딩할 수 없는 세그먼트가 있으면 예외를 던진다.")
    void throws_when_SegmentIsNotValidBase64() {
        assertThatThrownBy(() -> CursorCodec.decode("not-base64!!!:::abc", 2))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
