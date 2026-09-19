package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetJpaEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetJpaRepository extends JpaRepository<PetJpaEntity, Long> {

    boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);

    List<PetJpaEntity> findByMemberIdAndDeletedAtIsNull(Long memberId);

    Optional<PetJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PetJpaEntity p where p.id = :petId and p.deletedAt is null")
    Optional<PetJpaEntity> findByIdAndDeletedAtIsNullForUpdate(@Param("petId") Long petId);
}
