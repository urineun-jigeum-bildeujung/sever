package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.PetConcern;

import java.util.List;

public interface PetConcernRepository {
    List<PetConcern> saveAll(List<PetConcern> petConcern);

    List<PetConcern> findByPetId(Long petId);

    void deleteByPetId(Long petId);
}
