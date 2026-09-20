package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.ReviewImage;
import java.util.List;

public interface ReviewImageRepository {

    List<ReviewImage> saveAll(List<ReviewImage> images);
}
