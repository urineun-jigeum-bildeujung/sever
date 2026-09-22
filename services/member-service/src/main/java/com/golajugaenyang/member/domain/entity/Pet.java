package com.golajugaenyang.member.domain.entity;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
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
    private double weight;
    private int bcs;
    private String image;
    private LocalDateTime deletedAt;
    private Long memberId;
    private Long breedId;
    private Instant createdAt;
    private Instant updatedAt;

    public Pet(Long id, boolean isDefault, String name, Sex sex,
        boolean isNeutered, Species species, int age, LocalDate birthDate,
        TargetBreedSize targetBreedSize, double weight, int bcs, String image, LocalDateTime deletedAt,
        Long memberId, Long breedId, Instant createdAt, Instant updatedAt){
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

    public Pet delete() {
        return new Pet(id, isDefault, name, sex, isNeutered, species, age, birthDate,
            targetBreedSize, weight, bcs, image, LocalDateTime.now(), memberId, breedId, createdAt, updatedAt);
    }

    public Pet withIsDefault(boolean isDefault) {
        return new Pet(id, isDefault, name, sex, isNeutered, species, age, birthDate,
            targetBreedSize, weight, bcs, image, deletedAt, memberId, breedId, createdAt, updatedAt);
    }

    public Pet withTargetBreedSize(TargetBreedSize targetBreedSize) {
        return new Pet(id, isDefault, name, sex, isNeutered, species, age, birthDate,
            targetBreedSize, weight, bcs, image, deletedAt, memberId, breedId, createdAt, updatedAt);
    }

    public Pet update(String name, Sex sex, Boolean isNeutered, Species species, Integer age,
        LocalDate birthDate, TargetBreedSize size, Double weight, Integer bcs, String image, Long breedId) {
        return new Pet(
            id,
            isDefault,
            name != null ? name : this.name,
            sex != null ? sex : this.sex,
            isNeutered != null ? isNeutered : this.isNeutered,
            species != null ? species : this.species,
            age != null ? age : this.age,
            birthDate != null ? birthDate : this.birthDate,
            size != null ? size : this.targetBreedSize,
            weight != null ? weight : this.weight,
            bcs != null ? bcs : this.bcs,
            image != null ? image : this.image,
            deletedAt,
            memberId,
            breedId != null ? breedId : this.breedId,
            createdAt,
            updatedAt
        );
    }
}
