package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.product.support.CursorCodec;

public interface CursorContext {

    String[] fingerprintSegments();

    default String fingerprint() {
        return CursorCodec.encode(fingerprintSegments());
    }
}
