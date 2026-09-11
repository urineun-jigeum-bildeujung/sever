package com.golajugaenyang.review.out.persistence.mapper;

import com.golajugaenyang.review.domain.entity.ReviewRecommend;
import com.golajugaenyang.review.out.persistence.entity.ReviewRecommendJpaEntity;

public class ReviewRecommendMapper {

    public static ReviewRecommend toDomain(ReviewRecommendJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new ReviewRecommend(
                jpaEntity.getId(),
                jpaEntity.getMemberId(),
                jpaEntity.getReviewId()
        );
    }

    public static ReviewRecommendJpaEntity toJpaEntity(ReviewRecommend domain) {
        if (domain == null) return null;
        return ReviewRecommendJpaEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .reviewId(domain.getReviewId())
                .build();
    }
}
