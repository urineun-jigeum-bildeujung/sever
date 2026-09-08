package com.golajugaenyang.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TargetAgeGroup {
    SMALL("소형"),
    MEDIUM("중형"),
    LARGE("대형");

    private final String displayName;
}
