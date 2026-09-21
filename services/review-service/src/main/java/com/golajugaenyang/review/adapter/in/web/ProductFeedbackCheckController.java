package com.golajugaenyang.review.adapter.in.web;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.review.adapter.in.web.dto.request.FeedbackSubmitRequest;
import com.golajugaenyang.review.adapter.in.web.dto.response.FeedbackCheckPendingListResponse;
import com.golajugaenyang.review.application.ProductFeedbackCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
public class ProductFeedbackCheckController {

    private final ProductFeedbackCheckService feedbackCheckService;

    @GetMapping("/feedbacks/pending")
    public ResponseEntity<FeedbackCheckPendingListResponse> getPendingFeedbackChecks(@MemberId Long memberId) {
        return ResponseEntity.ok(feedbackCheckService.getPendingFeedbackChecks(memberId));
    }

    @PostMapping("/products/{productId}/feedbacks")
    public ResponseEntity<Void> submitFeedback(
            @MemberId Long memberId,
            @PathVariable Long productId,
            @Valid @RequestBody FeedbackSubmitRequest request
    ) {
        feedbackCheckService.submitFeedback(
                memberId, productId, request.orderProductId(), request.postpone(), request.answer());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
