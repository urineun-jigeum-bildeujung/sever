package com.golajugaenyang.member.adapter.in.web.dto.response;

import java.time.LocalDate;

public record MemberMyProfileResponse(
        String nickname,
        String name,
        LocalDate birth,
        String phone,
        String image,
        String email
) {
}
