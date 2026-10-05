-- Empty historical lists never establish an explicit none declaration.
UPDATE pet SET allergy_profile_status = 'KNOWN_LIST'
WHERE EXISTS (SELECT 1 FROM pet_allergy WHERE pet_allergy.pet_id = pet.id);
