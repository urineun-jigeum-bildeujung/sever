package com.golajugaenyang.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum SubcategoryCode {
    DRY_FOOD(CategoryCode.FOOD, "건식 사료", 1),
    WET_FOOD(CategoryCode.FOOD, "습식 사료", 2),
    FREEZE_DRIED_FOOD(CategoryCode.FOOD, "동결건조 사료", 3),
    BAKED_FOOD(CategoryCode.FOOD, "구운 사료", 4),
    POWDER_SUPPLEMENT(CategoryCode.SUPPLEMENT, "파우더형 영양제", 1),
    LIQUID_SUPPLEMENT(CategoryCode.SUPPLEMENT, "액상형 영양제", 2),
    CHEWABLE_SUPPLEMENT(CategoryCode.SUPPLEMENT, "츄어블형 영양제", 3),
    TABLET_SUPPLEMENT(CategoryCode.SUPPLEMENT, "정제형 영양제", 4),
    JERKY_TREAT(CategoryCode.TREAT, "육포형 간식", 1),
    WET_TREAT(CategoryCode.TREAT, "습식/파우치형 간식", 2),
    FREEZE_DRIED_TREAT(CategoryCode.TREAT, "동결건조 간식", 3),
    BISCUIT_TREAT(CategoryCode.TREAT, "비스킷/저작형 간식", 4);

    private final CategoryCode category;
    private final String displayName;
    private final int sortOrder;
}
