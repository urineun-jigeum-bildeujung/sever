package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetAllergyJpaEntity;
import com.golajugaenyang.member.domain.entity.PetAllergy;

public class PetAllergyMapper {

    public static PetAllergy toDomain(PetAllergyJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new PetAllergy(
            jpaEntity.getId(),
            jpaEntity.getAllergyCode(),
            jpaEntity.getPetId()
        );
    }

    public static PetAllergyJpaEntity toJpaEntity(PetAllergy domain){
        if(domain == null) return null;
        return PetAllergyJpaEntity.builder()
            .id(domain.getId())
            .allergyCode(domain.getAllergyCode())
            .petId(domain.getPetId())
            .build();
    }

}
