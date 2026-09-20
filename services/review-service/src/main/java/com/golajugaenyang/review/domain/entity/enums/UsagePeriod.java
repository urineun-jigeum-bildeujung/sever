package com.golajugaenyang.review.domain.entity.enums;

import lombok.Getter;

@Getter
public enum UsagePeriod {
    ONE_MONTH("1개월"),
    THREE_MONTHS("3개월"),
    SIX_MONTHS("6개월"),
    ONE_YEAR("1년");

    private final String displayName;

    UsagePeriod(String displayName) {
        this.displayName = displayName;
    }
}
