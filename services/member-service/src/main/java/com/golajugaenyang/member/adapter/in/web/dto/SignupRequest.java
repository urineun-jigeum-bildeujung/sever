package com.golajugaenyang.member.adapter.in.web.dto;

import com.golajugaenyang.member.domain.entity.enums.AgreementType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SignupRequest(
    @NotBlank String nickname,
    @NotEmpty List<@Valid AgreementItem> agreements
) {

    public record AgreementItem(
        @NotNull AgreementType type,
        boolean agreed
    ) {
    }
}
