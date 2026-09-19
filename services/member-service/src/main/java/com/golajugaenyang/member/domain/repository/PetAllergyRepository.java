package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.PetAllergy;

import java.util.List;

public interface PetAllergyRepository {
    List<PetAllergy> saveAll(List<PetAllergy> petAllergies);

    List<PetAllergy> findByPetId(Long petId);

    void deleteByPetId(Long petId);

    void deleteAllById(List<Long> ids);
}
