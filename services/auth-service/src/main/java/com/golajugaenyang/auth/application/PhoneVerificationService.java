package com.golajugaenyang.auth.application;

import com.golajugaenyang.auth.adapter.in.web.response.PhoneVerificationSendResponse;
import com.golajugaenyang.auth.domain.error.AuthErrorCode;
import com.golajugaenyang.auth.security.jwt.PhoneVerificationStore;
import com.golajugaenyang.common.core.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final PhoneVerificationStore phoneVerificationStore;

    private static final int FIXED_CODE = 584937;
    private final long TTL_SECONDS = 180L;

    public PhoneVerificationSendResponse sendCode(String phone) {
        if (!phoneVerificationStore.allowRequest(phone)) {
            throw new AppException(AuthErrorCode.TOO_MANY_REQUESTS);
        }

        phoneVerificationStore.issue(phone, TTL_SECONDS);
        return new PhoneVerificationSendResponse((int) TTL_SECONDS);
    }

    public boolean confirmCode(String phone, int code) {
        if (!phoneVerificationStore.allowConfirmAttempt(phone)) {
            throw new AppException(AuthErrorCode.TOO_MANY_REQUESTS);
        }

        return code == FIXED_CODE && phoneVerificationStore.isValid(phone);
    }
}
