package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetConcernJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetConcernJpaRepository extends JpaRepository<PetConcernJpaEntity, Long> {

    List<PetConcernJpaEntity> findByPetId(Long petId);
}
