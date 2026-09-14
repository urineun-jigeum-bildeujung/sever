package com.golajugaenyang.member.adapter.in.web.dto;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

public record PetRegisterRequest(
    @NotBlank String name,
    @NotNull Sex sex,
    boolean isNeutered,
    @NotNull Species species,
    @NotNull @Positive Integer age,
    LocalDate birthDate,
    @NotNull TargetBreedSize size,
    @Positive double weight,
    @NotNull @Min(1) @Max(5) Integer bcs,
    String image,
    @NotNull Long breedId,
    List<String> healthConcerns,
    List<AllergenCode> allergies
) {
}
