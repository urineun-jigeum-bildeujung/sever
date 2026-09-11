package com.golajugaenyang.review.domain.entity;

import lombok.Getter;

@Getter
public class ReviewRecommend {

    private Long id;
    private Long memberId;
    private Long reviewId;

    public ReviewRecommend(Long id, Long memberId, Long reviewId) {
        this.id = id;
        this.memberId = memberId;
        this.reviewId = reviewId;
    }
}
