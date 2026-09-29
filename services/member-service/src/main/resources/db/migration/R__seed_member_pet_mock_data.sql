-- 재구매 예측(AI팀) 연동을 위한 member/pet 목데이터.
-- nickname이 'mock_member_', pet.name이 'mock_pet_'로 시작하는 행만 대상이라
-- 실데이터와 섞이지 않고, 재실행해도 안전(delete+reinsert)함.
-- member와 pet을 한 파일에서 같이 관리하는 이유: 파일을 나누면 member만 수정됐을 때
-- pet 파일은 체크섬이 그대로라 재실행되지 않아, 예전 member_id를 참조하는 고아 pet이
-- 남을 수 있음 -> 항상 같이 재생성되도록 하나의 repeatable migration으로 묶음.
-- breed_master/concern_master는 이미 실데이터(각 58/87건)가 있어 그대로 참조하고
-- 별도로 만들지 않음. AI팀 요청 문서 기준 member는 id만 참조하면 되므로 그 외
-- PII 컬럼(name/phone/birth 등)은 채우지 않음.

SELECT setseed(0.4271);

-- 1) member -------------------------------------------------------------
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

-- 2) pet (회원당 1~3마리, 강아지/고양이 랜덤) -------------------------------
DELETE FROM pet_allergy WHERE pet_id IN (SELECT id FROM pet WHERE LEFT(name, 9) = 'mock_pet_');
DELETE FROM pet_concern WHERE pet_id IN (SELECT id FROM pet WHERE LEFT(name, 9) = 'mock_pet_');
DELETE FROM pet WHERE LEFT(name, 9) = 'mock_pet_';

WITH mock_members AS MATERIALIZED (
    -- pet_count를 미리 컬럼으로 계산해둠: generate_series 인자 자리에서 바로
    -- random()/width_bucket()을 호출하면 Postgres가 행마다 재평가하지 않고
    -- 한 번만 계산해 전체 LATERAL 호출에 재사용하는 문제가 있어(실측 확인함).
    SELECT id AS member_id, width_bucket(random(), 0, 1, 3) AS pet_count
    FROM member
    WHERE LEFT(nickname, 12) = 'mock_member_'
),
pet_plan AS (
    SELECT
        mm.member_id,
        gs.slot_no,
        CASE WHEN random() < 0.5 THEN 'DOG' ELSE 'CAT' END AS species,
        current_date - floor(random() * 5475)::int AS birth_date
    FROM mock_members mm
    CROSS JOIN LATERAL generate_series(1, mm.pet_count) AS gs(slot_no)
)
INSERT INTO pet (
    created_at, updated_at, age, bcs, birth_date, breed_id, is_default,
    is_neutered, member_id, name, sex, species, target_breed_size, weight
)
SELECT
    now(),
    now(),
    extract(year FROM age(current_date, p.birth_date))::int,
    1 + floor(random() * 9)::int,
    p.birth_date,
    (SELECT id FROM breed_master b WHERE b.species = p.species ORDER BY random() LIMIT 1),
    (p.slot_no = 1),
    random() < 0.7,
    p.member_id,
    'mock_pet_' || lpad(p.member_id::text, 6, '0') || '_' || p.slot_no::text,
    CASE WHEN random() < 0.5 THEN 'MALE' ELSE 'FEMALE' END,
    p.species,
    CASE WHEN p.species = 'DOG' THEN (ARRAY['SMALL', 'MEDIUM', 'LARGE'])[1 + floor(random() * 3)::int] END,
    CASE WHEN p.species = 'CAT' THEN round((2.5 + random() * 4)::numeric, 2) ELSE round((2 + random() * 38)::numeric, 2) END
FROM pet_plan p;

-- 3) pet_allergy (반려동물별 0~3개, 없는 경우가 더 많게: 60/25/10/5%) --------
WITH mock_pets AS (
    SELECT id AS pet_id FROM pet WHERE LEFT(name, 9) = 'mock_pet_'
),
pet_allergy_target AS MATERIALIZED (
    SELECT pet_id,
        CASE
            WHEN r < 0.6 THEN 0
            WHEN r < 0.85 THEN 1
            WHEN r < 0.95 THEN 2
            ELSE 3
        END AS allergy_count
    FROM (SELECT pet_id, random() AS r FROM mock_pets) t
),
ranked_codes AS (
    SELECT
        t.pet_id,
        code,
        row_number() OVER (PARTITION BY t.pet_id ORDER BY random()) AS rn
    FROM pet_allergy_target t
    CROSS JOIN unnest(ARRAY[
        'CHICKEN', 'BEEF', 'PORK', 'LAMB', 'DUCK', 'TURKEY', 'RABBIT', 'VENISON', 'GOAT',
        'INSECT', 'KANGAROO', 'FISH', 'SALMON', 'TUNA', 'ANCHOVY', 'CRUSTACEAN', 'BONITO',
        'DAIRY', 'EGG', 'CHEESE', 'WHEY', 'CHOCOLATE', 'GRAPE_RAISIN', 'ONION', 'GARLIC',
        'WHEAT_GLUTEN', 'CORN', 'RICE', 'OAT_BARLEY', 'SOY', 'LENTIL', 'PEA', 'CHICKPEA',
        'POTATO', 'YEAST', 'SWEET_POTATO', 'TAPIOCA', 'OTHER'
    ]) AS code
)
INSERT INTO pet_allergy (allergy_code, pet_id)
SELECT rc.code, rc.pet_id
FROM ranked_codes rc
JOIN pet_allergy_target t ON t.pet_id = rc.pet_id
WHERE rc.rn <= t.allergy_count;

-- 4) pet_concern (반려동물별 0~3개, 종에 맞는 관심사만: 40/30/20/10%) --------
WITH mock_pets AS (
    SELECT id AS pet_id, species FROM pet WHERE LEFT(name, 9) = 'mock_pet_'
),
pet_concern_target AS MATERIALIZED (
    SELECT pet_id, species,
        CASE
            WHEN r < 0.4 THEN 0
            WHEN r < 0.7 THEN 1
            WHEN r < 0.9 THEN 2
            ELSE 3
        END AS concern_count
    FROM (SELECT pet_id, species, random() AS r FROM mock_pets) t
),
ranked_concerns AS (
    SELECT
        t.pet_id,
        cm.id AS concern_id,
        row_number() OVER (PARTITION BY t.pet_id ORDER BY random()) AS rn
    FROM pet_concern_target t
    JOIN concern_master cm ON cm.species = t.species
)
INSERT INTO pet_concern (concern_id, pet_id)
SELECT rc.concern_id, rc.pet_id
FROM ranked_concerns rc
JOIN pet_concern_target t ON t.pet_id = rc.pet_id
WHERE rc.rn <= t.concern_count;
