package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.ReviewImage;
import java.util.List;

public interface ReviewImageRepository {

    List<ReviewImage> saveAll(List<ReviewImage> images);

    List<ReviewImage> findRepresentativeImagesByReviewIds(List<Long> reviewIds);

    List<ReviewImage> findByReviewId(Long reviewId);

    List<ReviewImage> findByReviewIdIn(List<Long> reviewIds);

    List<ReviewImage> findByProductId(Long productId, int page, int size);

    long countByProductId(Long productId);
}
