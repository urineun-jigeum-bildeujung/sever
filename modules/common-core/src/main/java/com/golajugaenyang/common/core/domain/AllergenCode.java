package com.golajugaenyang.common.core.domain;


import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AllergenCode {
    // 육류 (CRITICAL)
    CHICKEN("닭고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    BEEF("소고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    PORK("돼지고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    LAMB("양고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    DUCK("오리고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    TURKEY("칠면조", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    RABBIT("토끼고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    VENISON("사슴고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    GOAT("염소고기", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG)),
    INSECT("곤충 단백질", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG)),
    KANGAROO("캥거루", AllergenGroup.MEAT, AllergenSeverity.CRITICAL,
        Set.of(Species.CAT)),

    // 해산물 (CRITICAL)
    FISH("생선", AllergenGroup.SEAFOOD, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    SALMON("연어", AllergenGroup.SEAFOOD, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    TUNA("참치", AllergenGroup.SEAFOOD, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    ANCHOVY("멸치·앤초비", AllergenGroup.SEAFOOD, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    CRUSTACEAN("갑각류", AllergenGroup.SEAFOOD, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    BONITO("가다랑어", AllergenGroup.SEAFOOD, AllergenSeverity.CRITICAL,
        Set.of(Species.CAT)),

    // 유제품·난류 (CRITICAL)
    DAIRY("유제품", AllergenGroup.DAIRY_EGG, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    EGG("계란", AllergenGroup.DAIRY_EGG, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    CHEESE("치즈", AllergenGroup.DAIRY_EGG, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    WHEY("유청", AllergenGroup.DAIRY_EGG, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),

    // 독성 물질 (CRITICAL)
    CHOCOLATE("초콜릿", AllergenGroup.TOXIC, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    GRAPE_RAISIN("포도·건포도", AllergenGroup.TOXIC, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    ONION("양파", AllergenGroup.TOXIC, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),
    GARLIC("마늘", AllergenGroup.TOXIC, AllergenSeverity.CRITICAL,
        Set.of(Species.DOG, Species.CAT)),

    // 곡류·콩류 (SEVERE)
    WHEAT_GLUTEN("밀·글루텐", AllergenGroup.GRAIN, AllergenSeverity.SEVERE,
        Set.of(Species.DOG, Species.CAT)),
    CORN("옥수수", AllergenGroup.GRAIN, AllergenSeverity.SEVERE,
        Set.of(Species.DOG, Species.CAT)),
    RICE("쌀", AllergenGroup.GRAIN, AllergenSeverity.SEVERE,
        Set.of(Species.DOG, Species.CAT)),
    OAT_BARLEY("귀리·보리", AllergenGroup.GRAIN, AllergenSeverity.SEVERE,
        Set.of(Species.DOG, Species.CAT)),
    SOY("대두", AllergenGroup.LEGUME, AllergenSeverity.SEVERE,
        Set.of(Species.DOG, Species.CAT)),
    LENTIL("렌틸콩", AllergenGroup.LEGUME, AllergenSeverity.SEVERE,
        Set.of(Species.DOG)),
    PEA("완두콩", AllergenGroup.LEGUME, AllergenSeverity.SEVERE,
        Set.of(Species.DOG, Species.CAT)),
    CHICKPEA("병아리콩", AllergenGroup.LEGUME, AllergenSeverity.SEVERE,
        Set.of(Species.CAT)),

    //특수 식물성 (MODERATE)
    POTATO("감자", AllergenGroup.PLANT, AllergenSeverity.MODERATE,
        Set.of(Species.DOG, Species.CAT)),
    YEAST("효모", AllergenGroup.PLANT, AllergenSeverity.MODERATE,
        Set.of(Species.DOG, Species.CAT)),
    SWEET_POTATO("고구마", AllergenGroup.PLANT, AllergenSeverity.MODERATE,
        Set.of(Species.DOG, Species.CAT)),
    TAPIOCA("타피오카", AllergenGroup.PLANT, AllergenSeverity.MODERATE,
        Set.of(Species.CAT)),

    // 기타
    OTHER("기타/직접 입력", AllergenGroup.ETC, AllergenSeverity.MODERATE,
        Set.of(Species.DOG, Species.CAT));

    private final String displayName;
    private final AllergenGroup group;
    private final AllergenSeverity severity;
    private final Set<Species> applicableSpecies;
}
