package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.ProductFeedbackCheck;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ProductFeedbackCheckRepository {

    Map<Long, ProductFeedbackCheck> findExistingByOrderProductIds(List<Long> orderProductIds);

    /**
     * @return 원자적으로 반영됐으면 true, 이미 ANSWERED 상태라 반영되지 않았으면 false
     */
    boolean submitAnswer(Long memberId, Long productId, Long orderProductId, FeedbackCheckAnswer answer);

    /**
     * @return 원자적으로 반영됐으면 true, 이미 ANSWERED 상태라 반영되지 않았으면 false
     */
    boolean postpone(Long memberId, Long productId, Long orderProductId, Instant postponedUntil);
}
