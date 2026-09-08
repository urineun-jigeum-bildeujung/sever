package com.golajugaenyang.member.domain.entity;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class Pet {

    private Long id;
    private boolean isDefault;
    private String name;
    private Sex sex;
    private boolean isNeutered;
    private Species species;
    private int age;
    private LocalDate birthDate;
    private TargetBreedSize targetBreedSize;
    private String weight;
    private int bcs;
    private String image;
    private LocalDateTime deletedAt;
    private Long memberId;
    private Long breedId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Pet(Long id, boolean isDefault, String name, Sex sex,
        boolean isNeutered, Species species, int age, LocalDate birthDate,
        TargetBreedSize targetBreedSize, String weight, int bcs, String image, LocalDateTime deletedAt,
        Long memberId, Long breedId, OffsetDateTime createdAt, OffsetDateTime updatedAt){
        this.id = id;
        this.isDefault = isDefault;
        this.name = name;
        this.sex = sex;
        this.isNeutered = isNeutered;
        this.species = species;
        this.age = age;
        this.birthDate = birthDate;
        this.targetBreedSize = targetBreedSize;
        this.weight = weight;
        this.bcs = bcs;
        this.image = image;
        this.deletedAt = deletedAt;
        this.memberId = memberId;
        this.breedId = breedId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
