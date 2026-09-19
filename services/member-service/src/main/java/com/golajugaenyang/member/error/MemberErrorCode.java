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
    DUPLICATE_AGREEMENT_TYPE(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_DUPLICATE_AGREEMENT_TYPE", "중복된 약관 항목이 있습니다."
    ),
    UNAUTHENTICATED(
        HttpStatus.UNAUTHORIZED,
        "MEMBER_401_UNAUTHORIZED", "인증에 실패했습니다."
    ),
    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MEMBER_404_NOT_FOUND", "회원을 찾을 수 없습니다."
    ),
    NOT_FOUND_ADDRESS(
            HttpStatus.NOT_FOUND,
            "MEMBER_404_NOT_FOUND_ADDRESS", "배송지를 찾을 수 없습니다."
    ),
    NOT_FOUND_PET(
            HttpStatus.NOT_FOUND,
            "MEMBER_404_NOT_FOUND_PET", "반려동물을 찾을 수 없습니다."
    ),
    INVALID_BREED(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_INVALID_BREED", "존재하지 않는 품종입니다."
    ),
    INVALID_CONCERN(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_INVALID_CONCERN", "존재하지 않는 관심사입니다."
    ),
    INVALID_ALLERGY(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_INVALID_ALLERGY", "선택한 종에 해당하지 않는 알레르기입니다."
    ),
    ALREADY_SIGNED_UP(
            HttpStatus.CONFLICT,
            "MEMBER_409_ALREADY_SIGNED_UP", "이미 등록되었습니다."
    ),
    ALREADY_HAVE_NICKNAME(
            HttpStatus.CONFLICT,
            "MEMBER_409_ALREADY_HAVE_NICKNAME", "중복된 닉네임입니다."
    ),
    LAST_DEFAULT_ADDRESS(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_LAST_DEFAULT_ADDRESS", "다른 배송지를 기본으로 설정해야 기본 배송지를 해제할 수 있습니다."
    ),
    INVALID_PHONE_CODE(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_INVALID_PHONE_CODE", "인증번호가 유효하지 않습니다."
    ),
    NOT_FOUND_PRODUCT(
            HttpStatus.NOT_FOUND,
            "MEMBER_404_NOT_FOUND_PRODUCT", "존재하지 않는 상품입니다."
    ),
    INVALID_IMAGE_EXTENSION(
            HttpStatus.BAD_REQUEST,
            "MEMBER_400_INVALID_IMAGE_EXTENSION", "허용되지 않는 이미지 형식입니다."
    );


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
