package com.golajugaenyang.review.out.persistence.mapper;

import com.golajugaenyang.review.domain.entity.ReviewQuestion;
import com.golajugaenyang.review.out.persistence.entity.ReviewQuestionJpaEntity;

public class ReviewQuestionMapper {

    public static ReviewQuestion toDomain(ReviewQuestionJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new ReviewQuestion(
                jpaEntity.getId(),
                jpaEntity.getReviewQuestionType(),
                jpaEntity.getReviewAnswer(),
                jpaEntity.getUpdatedAt(),
                jpaEntity.getReviewId()
        );
    }

    public static ReviewQuestionJpaEntity toJpaEntity(ReviewQuestion domain) {
        if (domain == null) return null;
        return ReviewQuestionJpaEntity.builder()
                .id(domain.getId())
                .reviewQuestionType(domain.getReviewQuestionType())
                .reviewAnswer(domain.getReviewAnswer())
                .updatedAt(domain.getUpdatedAt())
                .reviewId(domain.getReviewId())
                .build();
    }
}
