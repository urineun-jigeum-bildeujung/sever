package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, Long> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
