package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetConcernJpaEntity;
import com.golajugaenyang.member.domain.entity.PetConcern;

public class PetConcernMapper {

    public static PetConcern toDomain(PetConcernJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new PetConcern(
            jpaEntity.getId(),
            jpaEntity.getPetId(),
            jpaEntity.getConcernId()
        );
    }

    public static PetConcernJpaEntity toJpaEntity(PetConcern domain){
        if(domain == null) return null;
        return PetConcernJpaEntity.builder()
            .id(domain.getId())
            .petId(domain.getPetId())
            .concernId(domain.getConcernId())
            .build();
    }

}
