package com.golajugaenyang.review.adapter.in.web;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.review.adapter.in.web.dto.request.ReviewCreateRequest;
import com.golajugaenyang.review.adapter.in.web.dto.request.ReviewImageUploadRequest;
import com.golajugaenyang.review.adapter.in.web.dto.response.FeaturedReviewPhotosResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.MyReviewListResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewCreateResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewDetailResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewFilterListResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewImageUploadResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewPhotosResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewRecommendResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.WritableProductListResponse;
import com.golajugaenyang.review.application.ReviewService;
import com.golajugaenyang.review.domain.entity.Review;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
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

    @PostMapping("/images/presigned-url")
    public ResponseEntity<ReviewImageUploadResponse> issueImageUploadUrl(
            @MemberId Long memberId,
            @Valid @RequestBody ReviewImageUploadRequest request
    ) {
        ReviewImageUploadResponse response = reviewService.issueImageUploadUrl(memberId, request.extension());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}/photos/featured")
    public ResponseEntity<FeaturedReviewPhotosResponse> getFeaturedPhotos(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getFeaturedPhotos(productId));
    }

    @GetMapping("/products/{productId}/photos")
    public ResponseEntity<ReviewPhotosResponse> getPhotos(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "30") @Min(1) int size
    ) {
        return ResponseEntity.ok(reviewService.getPhotos(productId, page, size));
    }

    @PatchMapping("/{reviewId}/recommend")
    public ResponseEntity<ReviewRecommendResponse> toggleRecommend(
            @MemberId Long memberId,
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(reviewService.toggleRecommend(memberId, reviewId));
    }

    @GetMapping("/writable")
    public ResponseEntity<WritableProductListResponse> getWritableProducts(@MemberId Long memberId) {
        return ResponseEntity.ok(reviewService.getWritableProducts(memberId));
    }

    @GetMapping("/me")
    public ResponseEntity<MyReviewListResponse> getMyReviews(
            @MemberId Long memberId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return ResponseEntity.ok(reviewService.getMyReviews(memberId, page, size));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> getReviewDetail(
            @PathVariable Long reviewId,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId
    ) {
        return ResponseEntity.ok(reviewService.getReviewDetail(reviewId, memberId));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ReviewFilterListResponse> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long breedId,
            @RequestParam(required = false) String ageGroup,
            @RequestParam(required = false) Boolean neutered,
            @RequestParam(required = false) Integer weightMin,
            @RequestParam(required = false) Integer weightMax,
            @RequestParam(required = false) List<String> healthConcerns,
            @RequestParam(required = false) String usagePeriod,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "false") boolean personalized,
            @RequestParam(required = false) Long petId,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId
    ) {
        ReviewFilterListResponse response = reviewService.getProductReviews(
                productId, species, breedId, ageGroup, neutered, weightMin, weightMax,
                healthConcerns, usagePeriod, sort, page, size, personalized, petId, memberId);
        return ResponseEntity.ok(response);
    }
}
