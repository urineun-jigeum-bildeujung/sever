package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.PetAllergy;

import java.util.List;

public interface PetAllergyRepository {
    List<PetAllergy> saveAll(List<PetAllergy> petAllergies);
}
