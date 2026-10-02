-- PM 스펙: 리뷰 텍스트는 선택(0~300자)이다. 기존에는 @NotBlank로 필수였고, 검증 상한(300자)이
-- 실제 컬럼 길이(255자, Hibernate 기본값)보다 커서 256~300자 입력 시 DB에서 "value too long"으로
-- 터질 수 있었다(#202). 컬럼을 300자로 넓힌다. NOT NULL 제약은 원래 없어 별도 조치 불필요.
ALTER TABLE review ALTER COLUMN text TYPE character varying(300);
