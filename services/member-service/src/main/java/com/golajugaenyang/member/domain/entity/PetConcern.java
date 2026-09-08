package com.golajugaenyang.member.domain.entity;

import lombok.Getter;

@Getter
public class PetConcern {

    private Long id;
    private Long petId;
    private Long concernId;

    public PetConcern(Long id, Long petId, Long concernId) {
        this.id = id;
        this.petId = petId;
        this.concernId = concernId;
    }

}
