package com.golajugaenyang.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TargetAgeGroup {
    GROWTH("성장기"),
    ADULT("성견/성묘"),
    SENIOR("노령");

    private final String displayName;
}
