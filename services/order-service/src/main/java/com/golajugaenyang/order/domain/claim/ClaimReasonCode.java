package com.golajugaenyang.order.domain.claim;

import lombok.Getter;

@Getter
public enum ClaimReasonCode {
    CHANGE_OF_MIND,
    DAMAGED,
    WRONG_ITEM,
    NOT_AS_DESCRIBED,
    OTHER;

    public static ClaimReasonCode fromCode(String code) {
        try {
            return valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 신청 사유입니다: " + code, e);
        }
    }
}
