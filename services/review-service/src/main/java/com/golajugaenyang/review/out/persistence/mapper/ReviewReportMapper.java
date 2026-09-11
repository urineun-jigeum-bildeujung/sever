package com.golajugaenyang.review.out.persistence.mapper;

import com.golajugaenyang.review.domain.entity.ReviewReport;
import com.golajugaenyang.review.out.persistence.entity.ReviewReportJpaEntity;

public class ReviewReportMapper {

    public static ReviewReport toDomain(ReviewReportJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new ReviewReport(
                jpaEntity.getId(),
                jpaEntity.getReviewId(),
                jpaEntity.getMemberId(),
                jpaEntity.getReason()
        );
    }

    public static ReviewReportJpaEntity toJpaEntity(ReviewReport domain) {
        if (domain == null) return null;
        return ReviewReportJpaEntity.builder()
                .id(domain.getId())
                .reviewId(domain.getReviewId())
                .memberId(domain.getMemberId())
                .reason(domain.getReason())
                .build();
    }
}
