package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    boolean existsById(Long reviewId);

    Optional<Review> findById(Long reviewId);

    List<Long> findRecentReviewIdsWithImageByProductId(Long productId, int limit);

    List<Review> findByMemberId(Long memberId, int page, int size);
}
