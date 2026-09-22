package com.golajugaenyang.review.adapter.out.persistence.entity;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.review.domain.entity.enums.Sex;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPetSnapshotEmbeddable {

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species species;

    @Column(name = "breed_id", nullable = false)
    private Long breedId;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    @Column(nullable = false)
    private boolean neutered;

    @Enumerated(EnumType.STRING)
    private TargetBreedSize breedSize;

    @Column(nullable = false)
    private double weight;
}
