package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.ReviewRecommend;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewRecommendRepository {

    Optional<ReviewRecommend> findByMemberIdAndReviewId(Long memberId, Long reviewId);

    ReviewRecommend save(ReviewRecommend recommend);

    void deleteById(Long id);

    long countByReviewId(Long reviewId);

    Map<Long, Long> countByReviewIdIn(List<Long> reviewIds);
}
