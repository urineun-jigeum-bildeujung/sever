package com.golajugaenyang.review.out.persistence.mapper;

import com.golajugaenyang.review.domain.entity.ReviewImage;
import com.golajugaenyang.review.out.persistence.entity.ReviewImageJpaEntity;

public class ReviewImageMapper {

    public static ReviewImage toDomain(ReviewImageJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new ReviewImage(
                jpaEntity.getId(),
                jpaEntity.getImageUrl(),
                jpaEntity.getSortOrder(),
                jpaEntity.getReviewId()
        );
    }

    public static ReviewImageJpaEntity toJpaEntity(ReviewImage domain) {
        if (domain == null) return null;
        return ReviewImageJpaEntity.builder()
                .id(domain.getId())
                .imageUrl(domain.getImageUrl())
                .sortOrder(domain.getSortOrder())
                .reviewId(domain.getReviewId())
                .build();
    }
}
