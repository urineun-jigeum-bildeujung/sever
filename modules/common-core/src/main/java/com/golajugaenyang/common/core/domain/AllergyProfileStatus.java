package com.golajugaenyang.common.core.domain;

import java.util.List;

public enum AllergyProfileStatus {
    UNKNOWN, KNOWN_NONE, KNOWN_LIST;

    public static AllergyProfileStatus resolve(AllergyProfileStatus declared, List<?> allergies) {
        if (declared == null) {
            return allergies.isEmpty() ? UNKNOWN : KNOWN_LIST;
        }
        if ((declared == KNOWN_NONE && !allergies.isEmpty())
            || (declared == KNOWN_LIST && allergies.isEmpty())) {
            throw new IllegalArgumentException("ALLERGY_PROFILE_INCONSISTENT");
        }
        return declared;
    }
}
