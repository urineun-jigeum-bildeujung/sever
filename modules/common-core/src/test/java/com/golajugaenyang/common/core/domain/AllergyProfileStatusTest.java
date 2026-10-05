package com.golajugaenyang.common.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class AllergyProfileStatusTest {
    @Test
    void emptyLegacyListRemainsUnknown() {
        assertEquals(AllergyProfileStatus.UNKNOWN, AllergyProfileStatus.resolve(null, List.of()));
        assertEquals(AllergyProfileStatus.KNOWN_LIST,
            AllergyProfileStatus.resolve(null, List.of(AllergenCode.CHICKEN)));
    }

    @Test
    void explicitNoneRequiresEmptyList() {
        assertEquals(AllergyProfileStatus.KNOWN_NONE,
            AllergyProfileStatus.resolve(AllergyProfileStatus.KNOWN_NONE, List.of()));
        assertThrows(IllegalArgumentException.class, () -> AllergyProfileStatus.resolve(
            AllergyProfileStatus.KNOWN_NONE, List.of(AllergenCode.CHICKEN)));
        assertThrows(IllegalArgumentException.class, () -> AllergyProfileStatus.resolve(
            AllergyProfileStatus.KNOWN_LIST, List.of()));
    }
}
