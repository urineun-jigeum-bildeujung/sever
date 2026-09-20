package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.Review;
import java.util.List;

public interface ReviewRepository {

    Review save(Review review);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    List<Long> findRecentReviewIdsWithImageByProductId(Long productId, int limit);
}
