package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.Review;
import java.util.List;

public interface ReviewSearchRepository {

    List<Review> search(ReviewSearchCriteria criteria);

    long count(ReviewSearchCriteria criteria);

    Double averageRating(Long productId);
}
