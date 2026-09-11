package com.golajugaenyang.review.domain.entity;

import lombok.Getter;

@Getter
public class ReviewImage {

    private Long id;
    private String imageUrl;
    private int sortOrder;
    private Long reviewId;

    public ReviewImage(Long id, String imageUrl, int sortOrder, Long reviewId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.reviewId = reviewId;
    }

}
