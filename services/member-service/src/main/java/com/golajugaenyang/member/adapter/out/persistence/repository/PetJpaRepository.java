package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetJpaRepository extends JpaRepository<PetJpaEntity, Long> {

    boolean existsByMemberId(Long memberId);

    List<PetJpaEntity> findByMemberId(Long memberId);
}
