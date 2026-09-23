package com.golajugaenyang.review.adapter.in.internal;

import com.golajugaenyang.review.adapter.in.internal.dto.ProductRatingsInternalResponse;
import com.golajugaenyang.review.application.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/reviews")
@RequiredArgsConstructor
public class ReviewRatingInternalController {

    private final ReviewService reviewService;

    @GetMapping("/products/ratings")
    public ProductRatingsInternalResponse getProductRatings(@RequestParam("productIds") List<Long> productIds) {
        return reviewService.getProductRatings(productIds);
    }
}
