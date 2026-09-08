package com.golajugaenyang.member.domain.entity;

import com.golajugaenyang.common.core.domain.AllergenCode;
import lombok.Getter;

@Getter
public class PetAllergy {

    private Long id;
    private AllergenCode allergyCode;
    private Long petId;

    public PetAllergy(Long id, AllergenCode allergyCode, Long petId){
        this.id = id;
        this.allergyCode = allergyCode;
        this.petId = petId;
    }
}
