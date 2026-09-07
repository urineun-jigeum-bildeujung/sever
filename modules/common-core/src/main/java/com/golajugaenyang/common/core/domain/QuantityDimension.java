package com.golajugaenyang.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum QuantityDimension {
    MASS("중량"),
    VOLUME("부피"),
    COUNT("개수");

    private final String displayName;
}
