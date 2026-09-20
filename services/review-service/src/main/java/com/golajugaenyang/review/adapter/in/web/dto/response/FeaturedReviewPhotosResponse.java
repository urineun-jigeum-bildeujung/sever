package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.util.List;

public record FeaturedReviewPhotosResponse(
        List<Photo> photos
) {
    public record Photo(
            Long reviewId,
            String imageUrl
    ) {
    }
}
