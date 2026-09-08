package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.ConcernMasterJpaEntity;
import com.golajugaenyang.member.domain.entity.ConcernMaster;

public class ConcernMasterMapper {

    public static ConcernMaster toDomain(ConcernMasterJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new ConcernMaster(
            jpaEntity.getId(),
            jpaEntity.getSpecies(),
            jpaEntity.getConcernCategory(),
            jpaEntity.getConcernCode()
        );
    }

    public static ConcernMasterJpaEntity toJpaEntity(ConcernMaster domain){
        if(domain == null) return null;
        return ConcernMasterJpaEntity.builder()
            .id(domain.getId())
            .species(domain.getSpecies())
            .concernCategory(domain.getConcernCategory())
            .concernCode(domain.getConcernCode())
            .build();
    }

}
