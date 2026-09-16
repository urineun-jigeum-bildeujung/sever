package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.AddressJpaEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressJpaRepository extends JpaRepository<AddressJpaEntity, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<AddressJpaEntity> findByMemberIdAndIsDefaultTrue(Long memberId);

    List<AddressJpaEntity> findAllByMemberId(Long memberId);
}
