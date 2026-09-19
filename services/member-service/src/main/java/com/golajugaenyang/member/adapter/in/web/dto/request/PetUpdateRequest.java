package com.golajugaenyang.member.adapter.in.web.dto.request;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

public record PetUpdateRequest(
    String name,
    Sex sex,
    Boolean isNeutered,
    Species species,
    @Positive Integer age,
    @PastOrPresent LocalDate birthDate,
    TargetBreedSize size,
    @Positive Double weight,
    @Min(1) @Max(5) Integer bcs,
    String image,
    Long breedId,
    List<String> healthConcerns,
    List<AllergenCode> allergies
) {
}
