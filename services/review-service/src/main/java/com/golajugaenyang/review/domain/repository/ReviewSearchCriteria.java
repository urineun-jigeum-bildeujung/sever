package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.review.domain.entity.enums.AgeGroup;
import com.golajugaenyang.review.domain.entity.enums.ReviewSortType;
import com.golajugaenyang.review.domain.entity.enums.UsagePeriod;
import java.util.Set;

public record ReviewSearchCriteria(
        Long productId,
        Species species,
        Long breedId,
        AgeGroup ageGroup,
        Boolean neutered,
        Integer weightMin,
        Integer weightMax,
        Set<String> healthConcerns,
        UsagePeriod usagePeriod,
        ReviewSortType sort,
        int page,
        int size,
        Species personalizedSpecies,
        TargetBreedSize personalizedBreedSize
) {
}
