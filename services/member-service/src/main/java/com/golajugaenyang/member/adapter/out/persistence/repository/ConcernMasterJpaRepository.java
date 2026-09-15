package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.adapter.out.persistence.entity.ConcernMasterJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcernMasterJpaRepository extends JpaRepository<ConcernMasterJpaEntity, Long> {

    List<ConcernMasterJpaEntity> findByConcernCodeInAndSpecies(List<String> concernCodes, Species species);

    List<ConcernMasterJpaEntity> findBySpecies(Species species);

}
