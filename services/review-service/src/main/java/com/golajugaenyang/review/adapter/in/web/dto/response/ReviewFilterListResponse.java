package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ReviewFilterListResponse(
        double averageRating,
        int totalCount,
        List<Item> content
) {
    public record Item(
            Long reviewId,
            String nickname,
            Pet pet,
            int rating,
            String usagePeriod,
            String palatability,
            String text,
            List<String> images,
            int likeCount,
            LocalDate createdAt
    ) {
    }

    public record Pet(
            String name,
            String sex,
            int age,
            String breedSize,
            String species
    ) {
    }
}
