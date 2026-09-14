package com.golajugaenyang.member.adapter.in.web.dto.response;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import java.time.LocalDate;
import java.util.List;

public record PetDetailResponse(
    Long petId,
    String name,
    Species species,
    Long breedId,
    String breedName,
    int age,
    LocalDate birthDate,
    Sex sex,
    boolean isNeutered,
    TargetBreedSize size,
    double weight,
    int bcs,
    List<String> healthConcerns,
    List<String> allergies,
    String image,
    boolean isDefault
) {
}
