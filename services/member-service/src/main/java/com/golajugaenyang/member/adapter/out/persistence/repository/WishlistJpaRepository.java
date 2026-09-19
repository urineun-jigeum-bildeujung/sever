package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.WishlistJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistJpaRepository extends JpaRepository<WishlistJpaEntity, Long> {

    Optional<WishlistJpaEntity> findByMemberIdAndProductId(Long memberId, Long productId);

    List<WishlistJpaEntity> findByMemberId(Long memberId);

}
