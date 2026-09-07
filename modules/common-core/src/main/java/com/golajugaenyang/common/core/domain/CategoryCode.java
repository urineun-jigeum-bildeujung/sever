package com.golajugaenyang.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum CategoryCode {
    FOOD("사료", 1),
    TREAT("간식", 2),
    SUPPLEMENT("영양제", 3);

    private final String displayName;
    private final int sortOrder;
}
