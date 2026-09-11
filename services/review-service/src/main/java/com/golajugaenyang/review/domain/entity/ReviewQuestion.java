package com.golajugaenyang.review.domain.entity;

import com.golajugaenyang.review.domain.entity.enums.ReviewAnswer;
import com.golajugaenyang.review.domain.entity.enums.ReviewQuestionType;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ReviewQuestion {

    private Long id;
    private ReviewQuestionType reviewQuestionType;
    private ReviewAnswer reviewAnswer;
    private Instant updatedAt;
    private Long reviewId;

    public ReviewQuestion(Long id, ReviewQuestionType reviewQuestionType, ReviewAnswer reviewAnswer, Instant updatedAt, Long reviewId) {
        this.id = id;
        this.reviewQuestionType = reviewQuestionType;
        this.reviewAnswer = reviewAnswer;
        this.updatedAt = updatedAt;
        this.reviewId = reviewId;
    }

}
