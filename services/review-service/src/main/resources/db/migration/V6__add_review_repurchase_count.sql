-- review에 재구매 횟수(repurchase_count) 컬럼을 추가한다. 실제 재구매 이력 로직을 지금
-- 구현하기는 어려워, PM 요청대로 프론트 표시용 랜덤값을 대신 저장한다(#217). 평점이 높을수록
-- 재구매 횟수도 높게 나오도록 분포를 둔다:
--   평점 4~5점: 0=55%, 1=40%, 2=5%
--   평점 1~3점: 0=90%, 1=10%
-- 기존 리뷰 전체(목데이터 포함, 수만 건)도 같은 분포로 소급 백필한다.
ALTER TABLE review ADD COLUMN repurchase_count integer NOT NULL DEFAULT 0;

UPDATE review r
SET repurchase_count = CASE
    WHEN r.star_rate >= 4 THEN
        CASE WHEN t.roll < 0.55 THEN 0
             WHEN t.roll < 0.95 THEN 1
             ELSE 2 END
    ELSE
        CASE WHEN t.roll < 0.90 THEN 0
             ELSE 1 END
END
FROM (SELECT id, random() AS roll FROM review) t
WHERE r.id = t.id;
