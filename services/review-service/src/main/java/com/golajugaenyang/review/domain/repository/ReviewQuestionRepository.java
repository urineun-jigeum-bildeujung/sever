package com.golajugaenyang.review.domain.repository;

import com.golajugaenyang.review.domain.entity.ReviewQuestion;
import java.util.List;

public interface ReviewQuestionRepository {

    List<ReviewQuestion> saveAll(List<ReviewQuestion> questions);

    List<ReviewQuestion> findByReviewId(Long reviewId);
}
