package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewQuestionJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewQuestionJpaRepository extends JpaRepository<ReviewQuestionJpaEntity, Long> {

    List<ReviewQuestionJpaEntity> findByReviewId(Long reviewId);

    List<ReviewQuestionJpaEntity> findByReviewIdIn(List<Long> reviewIds);
}
