package com.golajugaenyang.member.adapter.in.web.dto.response;

public record PetSummaryResponse(
    Long petId,
    String name,
    String image,
    boolean isDefault
) {
}
