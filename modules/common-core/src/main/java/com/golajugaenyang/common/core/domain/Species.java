package com.golajugaenyang.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Species {
    DOG("강아지"),
    CAT("고양이");

    private final String displayName;
}
