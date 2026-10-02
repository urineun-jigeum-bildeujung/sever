-- 실제 배포 DB의 product_feedback_check.pet_id가 NOT NULL로 돼있으나, 엔티티(ProductFeedbackCheckJpaEntity.petId)와
-- V1 baseline 둘 다 nullable로 선언돼 있어 코드 의도와 불일치했다. order_items.pet_id가 null인 주문(펫 미지정 구매)에
-- 반응 체크를 제출하면 INSERT 시 제약조건 위반(#199)이 났다. 기존 데이터는 모두 non-null이라 영향 없음.
ALTER TABLE product_feedback_check ALTER COLUMN pet_id DROP NOT NULL;
