-- review 테이블에 실제 배포 DB에만 남아있는 옛날 단일-pet 구조의 흔적 컬럼 pet_id가 NOT NULL로
-- 남아있다. Review가 List<ReviewPetSnapshot> pets(review_pet 테이블)로 다중 반려동물을 지원하도록
-- 바뀌면서 이 컬럼은 더 이상 애플리케이션 코드 어디에서도 채워지지 않는데, 제약만 그대로 남아
-- 모든 리뷰 작성이 실패하고 있었다(#204). 로컬 DB는 V1 baseline 기준이라 이 컬럼 자체가 없어
-- 재현되지 않았다 — product_feedback_check.pet_id(V3)와 정확히 같은 패턴이다.
ALTER TABLE review ALTER COLUMN pet_id DROP NOT NULL;
