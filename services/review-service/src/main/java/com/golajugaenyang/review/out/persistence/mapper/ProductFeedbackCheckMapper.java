package com.golajugaenyang.review.out.persistence.mapper;

import com.golajugaenyang.review.domain.entity.ProductFeedbackCheck;
import com.golajugaenyang.review.out.persistence.entity.ProductFeedbackCheckJpaEntity;

public class ProductFeedbackCheckMapper {

    public static ProductFeedbackCheck toDomain(ProductFeedbackCheckJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new ProductFeedbackCheck(
                jpaEntity.getId(),
                jpaEntity.getFeedbackCheckStatus(),
                jpaEntity.getFeedbackCheckAnswer(),
                jpaEntity.getCheckAvailableAt(),
                jpaEntity.getCheckExpiresAt(),
                jpaEntity.getAnsweredAt(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getOrderProductId(),
                jpaEntity.getPetId()
        );
    }

    public static ProductFeedbackCheckJpaEntity toJpaEntity(ProductFeedbackCheck domain) {
        if (domain == null) return null;
        return ProductFeedbackCheckJpaEntity.builder()
                .id(domain.getId())
                .feedbackCheckStatus(domain.getFeedbackCheckStatus())
                .feedbackCheckAnswer(domain.getFeedbackCheckAnswer())
                .checkAvailableAt(domain.getCheckAvailableAt())
                .checkExpiresAt(domain.getCheckExpiresAt())
                .answeredAt(domain.getAnsweredAt())
                .orderProductId(domain.getOrderProductId())
                .petId(domain.getPetId())
                .build();
    }
}
