package com.golajugaenyang.order.domain.claim;

import lombok.Getter;

@Getter
public enum ClaimType {
    CANCEL,
    RETURN,
    EXCHANGE;

    public static ClaimType fromCode(String code) {
        try {
            return valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 클레임 유형입니다: " + code, e);
        }
    }
}
