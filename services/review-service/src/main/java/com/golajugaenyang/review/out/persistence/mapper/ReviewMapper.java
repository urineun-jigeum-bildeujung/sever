package com.golajugaenyang.review.out.persistence.mapper;

import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.out.persistence.entity.ReviewJpaEntity;

public class ReviewMapper {

    public static Review toDomain(ReviewJpaEntity jpaEntity) {
        if(jpaEntity == null) return null;
        return new Review(
                jpaEntity.getId(),
                jpaEntity.getText(),
                jpaEntity.getStarRate(),
                jpaEntity.getUsagePeriod(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedAt(),
                jpaEntity.getDeletedAt(),
                jpaEntity.getMemberId(),
                jpaEntity.getProductId(),
                jpaEntity.getPetId(),
                jpaEntity.getDataOrigin(),
                jpaEntity.isSynthetic(),
                jpaEntity.getDatasetRunId()
        );
    }

    public static ReviewJpaEntity toJpaEntity(Review domain){
        if(domain == null) return null;
        return ReviewJpaEntity.builder()
                .id(domain.getId())
                .text(domain.getText())
                .starRate(domain.getStarRate())
                .usagePeriod(domain.getUsagePeriod())
                .deletedAt(domain.getDeletedAt())
                .memberId(domain.getMemberId())
                .productId(domain.getProductId())
                .petId(domain.getPetId())
                .dataOrigin(domain.getDataOrigin())
                .isSynthetic(domain.isSynthetic())
                .datasetRunId(domain.getDatasetRunId())
                .build();
    }
}
