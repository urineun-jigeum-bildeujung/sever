package com.golajugaenyang.member.domain.entity;

import com.golajugaenyang.common.core.domain.Species;
import lombok.Getter;

@Getter
public class BreedMaster {

    private Long id;
    private Species species;
    private String breedName;

    public BreedMaster(Long id, Species species, String breedName) {
        this.id = id;
        this.species = species;
        this.breedName = breedName;
    }

}
