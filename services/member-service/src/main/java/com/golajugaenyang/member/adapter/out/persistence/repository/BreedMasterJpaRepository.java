package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.adapter.out.persistence.entity.BreedMasterJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreedMasterJpaRepository extends JpaRepository<BreedMasterJpaEntity, Long> {

    List<BreedMasterJpaEntity> findBySpecies(Species species);

}
