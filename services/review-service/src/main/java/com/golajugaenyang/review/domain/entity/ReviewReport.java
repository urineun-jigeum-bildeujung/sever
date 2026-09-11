package com.golajugaenyang.review.domain.entity;

import lombok.Getter;

@Getter
public class ReviewReport {

    private Long id;
    private Long reviewId;
    private Long memberId;
    private String reason;

    public ReviewReport(Long id, Long reviewId, Long memberId, String reason) {
        this.id = id;
        this.reviewId = reviewId;
        this.memberId = memberId;
        this.reason = reason;
    }

}
