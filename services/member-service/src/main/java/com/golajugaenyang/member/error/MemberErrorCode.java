package com.golajugaenyang.member.error;

import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    REQUIRED_AGREEMENT_NOT_AGREED(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_REQUIRED_AGREEMENT_NOT_AGREED", "필수 약관에 모두 동의해야 회원가입을 진행할 수 있습니다."
    ),
    UNAUTHENTICATED(
        HttpStatus.UNAUTHORIZED,
        "MEMBER_401_UNAUTHORIZED", "인증에 실패했습니다."
    ),
    ALREADY_SIGNED_UP(
            HttpStatus.CONFLICT,
            "MEMBER_402_ALREADY_SIGNED_UP", "이미 등록되었습니다."
    ),
    ALREADY_HAVE_NICKNAME(
            HttpStatus.CONFLICT,
            "MEMBER_403_ALREADY_HAVE_NICKNAME", "중복된 닉네임입니다."
    );


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
