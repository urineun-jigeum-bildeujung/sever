ALTER TABLE pet ADD COLUMN allergy_profile_status varchar(16) NOT NULL DEFAULT 'UNKNOWN';
ALTER TABLE pet ADD CONSTRAINT pet_allergy_profile_status_check
    CHECK (allergy_profile_status IN ('UNKNOWN', 'KNOWN_NONE', 'KNOWN_LIST'));

-- Existing rows establish a known list, but an empty list never proves none.
UPDATE pet SET allergy_profile_status = 'KNOWN_LIST'
WHERE EXISTS (SELECT 1 FROM pet_allergy WHERE pet_allergy.pet_id = pet.id);
