package com.golajugaenyang.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AllergenGroup {
    MEAT("육류"),
    SEAFOOD("어류·해산물"),
    DAIRY_EGG("유제품·난류"),
    TOXIC("독성 물질"),
    GRAIN("곡류"),
    LEGUME("콩류"),
    PLANT("특수 식물성"),
    ETC("기타");

    private final String displayName;
}
