package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ReviewDetailResponse(
        Long reviewId,
        boolean isMine,
        String nickname,
        Product product,
        List<Pet> pets,
        double rating,
        int usagePeriod,
        int repurchaseCount,
        boolean liked,
        int likeCount,
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

    public record Pet(
            Long petId,
            String name,
            String sex,
            int age,
            String breedSize,
            String species,
            Long breedId,
            String breedName,
            double weight
    ) {
    }

    public record AnswerValue(
            String questionKey,
            String answerValue
    ) {
    }
}
