package com.golajugaenyang.review.adapter.in.web;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.review.adapter.in.web.dto.request.ReviewCreateRequest;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewCreateResponse;
import com.golajugaenyang.review.application.ReviewService;
import com.golajugaenyang.review.domain.entity.Review;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewCreateResponse> createReview(
            @MemberId Long memberId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        Review savedReview = reviewService.createReview(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReviewCreateResponse(savedReview.getId()));
    }
}
