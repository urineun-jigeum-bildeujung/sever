package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.util.List;

public record ReviewPhotosResponse(
        int totalCount,
        List<Photo> photos,
        boolean hasNext
) {
    public record Photo(
            Long reviewId,
            String imageUrl
    ) {
    }
}
