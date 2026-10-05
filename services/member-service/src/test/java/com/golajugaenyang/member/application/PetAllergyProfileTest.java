package com.golajugaenyang.member.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.golajugaenyang.member.domain.entity.enums.AllergyProfileStatus;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.member.adapter.out.persistence.mapper.PetMapper;
import com.golajugaenyang.member.domain.entity.Pet;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import org.junit.jupiter.api.Test;

class PetAllergyProfileTest {
    @Test
    void profileSurvivesPersistenceAndUnrelatedEdits() {
        Pet pet = new Pet(1L, true, "test", Sex.MALE, true, Species.DOG, 3,
            null, TargetBreedSize.SMALL, 5, 3, null, null, 1L, 1L, null, null)
            .withAllergyProfileStatus(AllergyProfileStatus.KNOWN_NONE);
        assertEquals(AllergyProfileStatus.KNOWN_NONE,
            PetMapper.toDomain(PetMapper.toJpaEntity(pet)).getAllergyProfileStatus());
        assertEquals(AllergyProfileStatus.KNOWN_NONE, pet.withIsDefault(false).getAllergyProfileStatus());
        assertEquals(AllergyProfileStatus.KNOWN_NONE,
            pet.withTargetBreedSize(TargetBreedSize.LARGE).getAllergyProfileStatus());
        assertEquals(AllergyProfileStatus.KNOWN_NONE, pet.delete().getAllergyProfileStatus());
        assertEquals(AllergyProfileStatus.KNOWN_NONE,
            pet.update("updated", null, null, null, null, null, null, null, null, null, null)
                .getAllergyProfileStatus());
    }
}
