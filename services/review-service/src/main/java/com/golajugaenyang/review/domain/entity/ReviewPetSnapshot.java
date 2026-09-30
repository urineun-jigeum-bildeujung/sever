package com.golajugaenyang.review.domain.entity;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.review.domain.entity.enums.Sex;
import lombok.Getter;

@Getter
public class ReviewPetSnapshot {

    private final Long petId;
    private final String name;
    private final Species species;
    private final Long breedId;
    private final String breedName;
    private final int age;
    private final Sex sex;
    private final boolean neutered;
    private final TargetBreedSize breedSize;
    private final double weight;

    public ReviewPetSnapshot(Long petId, String name, Species species, Long breedId, String breedName, int age,
            Sex sex, boolean neutered, TargetBreedSize breedSize, double weight) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.breedId = breedId;
        this.breedName = breedName;
        this.age = age;
        this.sex = sex;
        this.neutered = neutered;
        this.breedSize = breedSize;
        this.weight = weight;
    }
}
