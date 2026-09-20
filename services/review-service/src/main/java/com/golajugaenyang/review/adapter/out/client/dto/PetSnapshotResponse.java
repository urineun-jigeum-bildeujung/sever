package com.golajugaenyang.review.adapter.out.client.dto;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.review.domain.entity.enums.Sex;
import java.util.List;

public record PetSnapshotResponse(
        String name,
        Species species,
        Long breedId,
        int age,
        Sex sex,
        boolean isNeutered,
        TargetBreedSize size,
        double weight,
        List<String> healthConcerns
) {
}
