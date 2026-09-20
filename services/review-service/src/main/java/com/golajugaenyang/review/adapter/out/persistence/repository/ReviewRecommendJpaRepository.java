package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewRecommendJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRecommendJpaRepository extends JpaRepository<ReviewRecommendJpaEntity, Long> {

    Optional<ReviewRecommendJpaEntity> findByMemberIdAndReviewId(Long memberId, Long reviewId);

    long countByReviewId(Long reviewId);
}
