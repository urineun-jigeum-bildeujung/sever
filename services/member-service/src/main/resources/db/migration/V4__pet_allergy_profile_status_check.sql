ALTER TABLE pet ADD CONSTRAINT pet_allergy_profile_status_check
    CHECK (allergy_profile_status IN ('UNKNOWN', 'KNOWN_NONE', 'KNOWN_LIST')) NOT VALID;
