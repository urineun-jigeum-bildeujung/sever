package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewRecommendJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRecommendJpaRepository extends JpaRepository<ReviewRecommendJpaEntity, Long> {

    Optional<ReviewRecommendJpaEntity> findByMemberIdAndReviewId(Long memberId, Long reviewId);

    long countByReviewId(Long reviewId);

    @Query("""
        select ri.reviewId as reviewId, count(ri) as count
        from ReviewRecommendJpaEntity ri
        where ri.reviewId in :reviewIds
        group by ri.reviewId
        """)
    List<ReviewLikeCountProjection> countByReviewIdIn(@Param("reviewIds") List<Long> reviewIds);
}
