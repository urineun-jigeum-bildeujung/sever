package com.golajugaenyang.common.core.domain;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum CautionIngredientCode {
    // 독성 (추천 제외 대상)
    XYLITOL("자일리톨", CautionLevel.TOXIC, Set.of(Species.DOG)),
    CHOCOLATE_CACAO("초콜릿·카카오", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    GRAPE_RAISIN("포도·건포도", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    ONION("양파", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    GARLIC("마늘", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    ALLIUM("부추·쪽파류", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    MACADAMIA("마카다미아", CautionLevel.TOXIC, Set.of(Species.DOG)),
    ALCOHOL("알코올", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    CAFFEINE("카페인", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    AVOCADO("아보카도", CautionLevel.TOXIC, Set.of(Species.DOG)),
    FRUIT_PITS("과일 씨앗·핵과류", CautionLevel.TOXIC, Set.of(Species.DOG)),
    NUTMEG_SPICE("육두구·향신료", CautionLevel.TOXIC, Set.of(Species.DOG)),
    RAW_YEAST_DOUGH("생효모 반죽", CautionLevel.TOXIC, Set.of(Species.DOG, Species.CAT)),
    CITRUS("시트러스 추출물", CautionLevel.TOXIC, Set.of(Species.CAT)),

    // 섭취 주의 (건강 상태에 따라 경고)
    HIGH_FAT("고지방", CautionLevel.CONDITIONAL, Set.of(Species.DOG, Species.CAT)),
    HIGH_SODIUM("고염분", CautionLevel.CONDITIONAL, Set.of(Species.DOG, Species.CAT)),
    LACTOSE_DAIRY("유제품·유당", CautionLevel.CONDITIONAL, Set.of(Species.DOG, Species.CAT)),
    EXCESS_FISH("생선 위주 과다급여", CautionLevel.CONDITIONAL, Set.of(Species.CAT)),
    RAW_FISH("날생선", CautionLevel.CONDITIONAL, Set.of(Species.CAT)),
    RAW_EGG_WHITE("날달걀 흰자", CautionLevel.CONDITIONAL, Set.of(Species.CAT)),
    EXCESS_LIVER("간 과다급여", CautionLevel.CONDITIONAL, Set.of(Species.CAT)),
    DOG_FOOD("개 사료", CautionLevel.CONDITIONAL, Set.of(Species.CAT)),

    // 영양 필수 미충족
    TAURINE_DEFICIENCY("타우린 부족", CautionLevel.NUTRITION, Set.of(Species.CAT));

    private final String displayName;
    private final CautionLevel cautionLevel;
    private final Set<Species> applicableSpecies;
}
