package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetAllergyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetAllergyJpaRepository extends JpaRepository<PetAllergyJpaEntity, Long> {

    List<PetAllergyJpaEntity> findByPetId(Long petId);
}
