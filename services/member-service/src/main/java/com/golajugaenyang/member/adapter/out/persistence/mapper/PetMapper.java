package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetJpaEntity;
import com.golajugaenyang.member.domain.entity.Pet;

public class PetMapper {

    public static Pet toDomain(PetJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new Pet(
            jpaEntity.getId(),
            jpaEntity.isDefault(),
            jpaEntity.getName(),
            jpaEntity.getSex(),
            jpaEntity.isNeutered(),
            jpaEntity.getSpecies(),
            jpaEntity.getAge(),
            jpaEntity.getBirthDate(),
            jpaEntity.getTargetBreedSize(),
            jpaEntity.getWeight(),
            jpaEntity.getBcs(),
            jpaEntity.getImage(),
            jpaEntity.getDeletedAt(),
            jpaEntity.getMemberId(),
            jpaEntity.getBreedId(),
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt()
        );
    }

    public static PetJpaEntity toJpaEntity(Pet domain){
        if(domain == null) return null;
        return PetJpaEntity.builder()
            .id(domain.getId())
            .isDefault(domain.isDefault())
            .name(domain.getName())
            .sex(domain.getSex())
            .isNeutered(domain.isNeutered())
            .species(domain.getSpecies())
            .age(domain.getAge())
            .birthDate(domain.getBirthDate())
            .targetBreedSize(domain.getTargetBreedSize())
            .weight(domain.getWeight())
            .bcs(domain.getBcs())
            .image(domain.getImage())
            .deletedAt(domain.getDeletedAt())
            .memberId(domain.getMemberId())
            .breedId(domain.getBreedId())
            .build();
    }
}
