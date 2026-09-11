package com.golajugaenyang.review.domain.entity;

import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ProductFeedbackCheck {

    private Long id;
    private FeedbackCheckStatus feedbackCheckStatus;
    private FeedbackCheckAnswer feedbackCheckAnswer;
    private Instant checkAvailableAt;
    private Instant checkExpiresAt;
    private Instant answeredAt;
    private Instant createdAt;
    private Long orderProductId;
    private Long petId;

    public ProductFeedbackCheck(Long id, FeedbackCheckStatus feedbackCheckStatus, FeedbackCheckAnswer feedbackCheckAnswer,
                                Instant checkAvailableAt, Instant checkExpiresAt,
                                Instant answeredAt, Instant createdAt, Long orderProductId, Long petId) {
        this.id = id;
        this.feedbackCheckStatus = feedbackCheckStatus;
        this.feedbackCheckAnswer = feedbackCheckAnswer;
        this.checkAvailableAt = checkAvailableAt;
        this.checkExpiresAt = checkExpiresAt;
        this.answeredAt = answeredAt;
        this.createdAt = createdAt;
        this.orderProductId = orderProductId;
        this.petId = petId;
    }

}
