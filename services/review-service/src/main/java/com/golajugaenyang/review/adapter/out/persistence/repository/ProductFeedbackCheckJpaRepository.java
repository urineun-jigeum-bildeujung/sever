package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ProductFeedbackCheckJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFeedbackCheckJpaRepository extends JpaRepository<ProductFeedbackCheckJpaEntity, Long> {

    Optional<ProductFeedbackCheckJpaEntity> findByOrderProductId(Long orderProductId);

    List<ProductFeedbackCheckJpaEntity> findByOrderProductIdIn(List<Long> orderProductIds);
}
