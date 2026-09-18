package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetJpaRepository extends JpaRepository<PetJpaEntity, Long> {

    boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);

    List<PetJpaEntity> findByMemberIdAndDeletedAtIsNull(Long memberId);

    Optional<PetJpaEntity> findByIdAndDeletedAtIsNull(Long id);
}
