package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ReviewDetailResponse(
        Long reviewId,
        boolean isMine,
        Product product,
        Long petId,
        int rating,
        int usagePeriod,
        List<AnswerValue> answerValues,
        List<String> goodPoints,
        List<String> badPoints,
        Integer matchScore,
        String text,
        List<String> images,
        LocalDate createdAt
) {
    public record Product(
            Long productId,
            String name,
            String image
    ) {
    }

    public record AnswerValue(
            String questionKey,
            String answerValue
    ) {
    }
}
