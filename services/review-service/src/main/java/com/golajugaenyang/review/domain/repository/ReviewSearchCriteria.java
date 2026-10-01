package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.review.domain.entity.enums.ReviewSortType;
import java.util.Set;

public record ReviewSearchCriteria(
        Long productId,
        Species species,
        Set<Long> breedIds,
        Integer ageMin,
        Integer ageMax,
        Boolean neutered,
        Integer weightMin,
        Integer weightMax,
        Set<String> healthConcerns,
        Integer usagePeriodMinDays,
        Integer usagePeriodMaxDays,
        ReviewSortType sort,
        int page,
        int size,
        Species personalizedSpecies,
        Integer personalizedAge,
        Double personalizedWeight,
        Boolean personalizedNeutered,
        TargetBreedSize personalizedBreedSize,
        Set<String> personalizedHealthConcerns
) {
}
