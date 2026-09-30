-- review_pet은 리뷰 작성 시점의 반려동물 상태를 스냅샷으로 저장하는 테이블.
-- 지금까지 breed_id만 저장해서, 품종명을 보여주려면 매번 member-service를 다시 호출해야 했음.
-- species/sex/weight/age처럼 breed_name도 스냅샷으로 같이 저장하도록 컬럼을 추가한다.
-- 기존 행은 값이 없으므로 nullable로 추가하고, 별도 백필 스크립트로 채운다.
ALTER TABLE review_pet ADD COLUMN breed_name character varying(255);
