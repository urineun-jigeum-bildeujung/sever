package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.Review;
import java.util.List;

public interface ReviewRepository {

    Review save(Review review);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    boolean existsById(Long reviewId);

    List<Long> findRecentReviewIdsWithImageByProductId(Long productId, int limit);

    List<Review> findByMemberId(Long memberId, int page, int size);
}
