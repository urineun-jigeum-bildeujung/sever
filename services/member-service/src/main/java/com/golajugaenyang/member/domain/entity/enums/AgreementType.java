package com.golajugaenyang.member.domain.entity.enums;

public enum AgreementType {
    SERVICE_TERMS(true), //사용자 이용약관 동의
    PRIVACY_COLLECTION(true), //개인정보 수집 및 이용 동의
    AGE_OVER_14(true), //만 14세 이상 확인
    MARKETING_BENEFIT(false), //맞춤혜택 및 이벤트 알림 수신 동의
    THIRD_PARTY_PROVIDE(false); //맞춤형 제휴 혜택을 위한 개인정보 제3자 제공 동의

    private final boolean required;

    AgreementType(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }
}
