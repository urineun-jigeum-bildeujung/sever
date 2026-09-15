package com.golajugaenyang.member.adapter.in.web.dto.response;

import com.golajugaenyang.common.core.domain.Species;

public record PetRegisterResponse(
        Long petId,
        String name,
        Species species,
        boolean isDefault,
        Long breedId
) {
}
