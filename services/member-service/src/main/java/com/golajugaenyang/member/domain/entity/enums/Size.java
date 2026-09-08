package com.golajugaenyang.member.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Size {
    SMALL("소형견", "10kg 미만"),
    MEDIUM("중형견", "10kg ~ 25kg"),
    LARGE("대형견", "25kg 이상");

    private final String displayName;
    private final String description;
}
