package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.MemberJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    boolean existsByNickname(String nickname);

    Optional<MemberJpaEntity> findByNickname(String nickname);

    Optional<MemberJpaEntity> findByAuthId(Long authId);

    Optional<MemberJpaEntity> findByAuthIdAndDeletedAtIsNull(Long authId);

    Optional<MemberJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MemberJpaEntity m where m.id = :memberId")
    Optional<MemberJpaEntity> findByIdForUpdate(@Param("memberId") Long memberId);

}
