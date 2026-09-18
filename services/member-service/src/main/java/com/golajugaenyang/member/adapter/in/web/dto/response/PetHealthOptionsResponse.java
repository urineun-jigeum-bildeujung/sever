package com.golajugaenyang.member.adapter.in.web.dto.response;

import java.util.List;

public record PetHealthOptionsResponse(
    List<CategoryOption> categories,
    List<AllergyOption> allergies
) {

    public record CategoryOption(String category, List<String> items) {
    }

}
