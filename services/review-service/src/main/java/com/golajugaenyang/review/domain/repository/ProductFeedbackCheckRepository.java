package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.ProductFeedbackCheck;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ProductFeedbackCheckRepository {

    Map<Long, ProductFeedbackCheck> findExistingByOrderProductIds(List<Long> orderProductIds);

    boolean isAlreadyAnswered(Long orderProductId);

    void submitAnswer(Long memberId, Long productId, Long orderProductId, FeedbackCheckAnswer answer);

    void postpone(Long memberId, Long productId, Long orderProductId, Instant postponedUntil);
}
