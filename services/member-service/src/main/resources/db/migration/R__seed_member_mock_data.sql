-- member 목데이터. nickname이 'mock_member_'로
-- 시작하는 행만 대상이라 실데이터와 섞이지 않고, 재실행해도 안전(delete+reinsert)함.
-- AI팀 요청 문서 기준 member는 id만 참조하면 되므로 그 외 PII 컬럼은 채우지 않음.
-- 파일을 고쳐서 다시 배포/재기동하면 아래 내용으로 갱신됨(Repeatable migration).

SELECT setseed(0.4271);

DELETE FROM member WHERE LEFT(nickname, 12) = 'mock_member_';

INSERT INTO member (created_at, updated_at, auth_id, nickname)
SELECT
    ts,
    ts,
    9000000 + n,
    'mock_member_' || lpad(n::text, 5, '0')
FROM (
    SELECT n, now() - (random() * interval '730 days') AS ts
    FROM generate_series(1, 6600) AS n
) g;
