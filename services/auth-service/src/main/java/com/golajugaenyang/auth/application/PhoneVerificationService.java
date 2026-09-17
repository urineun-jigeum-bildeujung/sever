package com.golajugaenyang.auth.application;

import com.golajugaenyang.auth.adapter.in.web.response.PhoneVerificationSendResponse;
import com.golajugaenyang.auth.security.jwt.PhoneVerificationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final PhoneVerificationStore phoneVerificationStore;

    private static final int FIXED_CODE = 584937;
    private final long TTL_SECONDS = 180L;

    public PhoneVerificationSendResponse sendCode(String phone) {
        phoneVerificationStore.issue(phone, TTL_SECONDS);
        return new PhoneVerificationSendResponse((int) TTL_SECONDS);
    }

    public boolean confirmCode(String phone, int code) {
        return code == FIXED_CODE && phoneVerificationStore.isValid(phone);
    }
}
