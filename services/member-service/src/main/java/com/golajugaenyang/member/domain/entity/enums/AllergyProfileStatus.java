package com.golajugaenyang.member.domain.entity.enums;

import java.util.List;

public enum AllergyProfileStatus {
    UNKNOWN, KNOWN_NONE, KNOWN_LIST;

    public static AllergyProfileStatus resolve(AllergyProfileStatus declared, List<?> allergies) {
        if (declared == null) {
            return allergies.isEmpty() ? UNKNOWN : KNOWN_LIST;
        }
        if ((declared != KNOWN_LIST && !allergies.isEmpty())
            || (declared == KNOWN_LIST && allergies.isEmpty())) {
            throw new IllegalArgumentException("ALLERGY_PROFILE_INCONSISTENT");
        }
        return declared;
    }
}
