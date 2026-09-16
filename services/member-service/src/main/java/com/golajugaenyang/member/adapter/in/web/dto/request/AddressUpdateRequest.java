package com.golajugaenyang.member.adapter.in.web.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "빈 값으로는 수정할 수 없습니다.") String addressName,
        @Pattern(regexp = ".*\\S.*", message = "빈 값으로는 수정할 수 없습니다.") String receiver,
        @Pattern(regexp = ".*\\S.*", message = "빈 값으로는 수정할 수 없습니다.") String phone,
        @Pattern(regexp = ".*\\S.*", message = "빈 값으로는 수정할 수 없습니다.") String zipCode,
        @Pattern(regexp = ".*\\S.*", message = "빈 값으로는 수정할 수 없습니다.") String address,
        @Pattern(regexp = ".*\\S.*", message = "빈 값으로는 수정할 수 없습니다.") String addressDetail,
        @Size(max = 100) String deliveryNote,
        Boolean isDefault
) {
}
