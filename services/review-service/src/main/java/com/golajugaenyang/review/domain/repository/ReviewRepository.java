package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.Review;

public interface ReviewRepository {

    Review save(Review review);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
