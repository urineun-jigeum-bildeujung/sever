package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.ConcernMasterJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcernMasterJpaRepository extends JpaRepository<ConcernMasterJpaEntity, Long> {

    List<ConcernMasterJpaEntity> findByConcernCodeIn(List<String> concernCodes);

}
