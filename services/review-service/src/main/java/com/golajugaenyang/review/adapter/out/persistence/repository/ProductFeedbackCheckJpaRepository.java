package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ProductFeedbackCheckJpaEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductFeedbackCheckJpaRepository extends JpaRepository<ProductFeedbackCheckJpaEntity, Long> {

    List<ProductFeedbackCheckJpaEntity> findByOrderProductIdIn(List<Long> orderProductIds);

    /**
     * ANSWERED가 아닌 경우에만 답변을 반영하는 원자적 upsert.
     * 이미 ANSWERED면 WHERE 절에 막혀 아무 행도 갱신되지 않는다(영향받은 행 수 0).
     */
    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO product_feedback_check
            (member_id, product_id, order_product_id, pet_id, feedback_check_status, feedback_check_answer,
             answered_at, created_at, updated_at)
        VALUES (:memberId, :productId, :orderProductId, :petId, 'ANSWERED', :answer, now(), now(), now())
        ON CONFLICT ON CONSTRAINT uk_product_feedback_check_order_product DO UPDATE
        SET feedback_check_status = 'ANSWERED',
            feedback_check_answer = EXCLUDED.feedback_check_answer,
            pet_id = EXCLUDED.pet_id,
            answered_at = now(),
            updated_at = now()
        WHERE product_feedback_check.feedback_check_status <> 'ANSWERED'
        """, nativeQuery = true)
    int upsertAnswer(
            @Param("memberId") Long memberId,
            @Param("productId") Long productId,
            @Param("orderProductId") Long orderProductId,
            @Param("petId") Long petId,
            @Param("answer") String answer);

    /**
     * ANSWERED가 아닌 경우에만 보류를 반영하는 원자적 upsert.
     * 이미 ANSWERED면 WHERE 절에 막혀 아무 행도 갱신되지 않는다(영향받은 행 수 0).
     */
    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO product_feedback_check
            (member_id, product_id, order_product_id, feedback_check_status, check_available_at,
             created_at, updated_at)
        VALUES (:memberId, :productId, :orderProductId, 'POSTPONED', :postponedUntil, now(), now())
        ON CONFLICT ON CONSTRAINT uk_product_feedback_check_order_product DO UPDATE
        SET feedback_check_status = 'POSTPONED',
            check_available_at = EXCLUDED.check_available_at,
            updated_at = now()
        WHERE product_feedback_check.feedback_check_status <> 'ANSWERED'
        """, nativeQuery = true)
    int upsertPostpone(
            @Param("memberId") Long memberId,
            @Param("productId") Long productId,
            @Param("orderProductId") Long orderProductId,
            @Param("postponedUntil") Instant postponedUntil);
}
