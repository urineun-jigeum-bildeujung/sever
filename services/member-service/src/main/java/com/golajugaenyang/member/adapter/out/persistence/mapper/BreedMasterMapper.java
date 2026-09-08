package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.BreedMasterJpaEntity;
import com.golajugaenyang.member.domain.entity.BreedMaster;

public class BreedMasterMapper {

    public static BreedMaster toDomain(BreedMasterJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new BreedMaster(
            jpaEntity.getId(),
            jpaEntity.getSpecies(),
            jpaEntity.getBreedName()
        );
    }

    public static BreedMasterJpaEntity toJpaEntity(BreedMaster domain){
        if(domain == null) return null;
        return BreedMasterJpaEntity.builder()
            .id(domain.getId())
            .species(domain.getSpecies())
            .breedName(domain.getBreedName())
            .build();
    }

}
