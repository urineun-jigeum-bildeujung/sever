package com.golajugaenyang.auth.adapter.in.web;

import com.golajugaenyang.auth.adapter.in.web.request.PhoneVerificationConfirmRequest;
import com.golajugaenyang.auth.adapter.in.web.request.PhoneVerificationSendRequest;
import com.golajugaenyang.auth.adapter.in.web.response.PhoneVerificationConfirmResponse;
import com.golajugaenyang.auth.adapter.in.web.response.PhoneVerificationSendResponse;
import com.golajugaenyang.auth.application.PhoneVerificationService;
import com.golajugaenyang.common.security.annotation.AuthId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auths/phone")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/verify-request")
    public PhoneVerificationSendResponse verifyRequest(
            @AuthId Long authId,
            @Valid @RequestBody PhoneVerificationSendRequest request) {
        return phoneVerificationService.sendCode(request.phone());
    }

    @PostMapping("/verify-confirm")
    public PhoneVerificationConfirmResponse verifyConfirm(
            @AuthId Long authId,
            @Valid @RequestBody PhoneVerificationConfirmRequest request) {
        boolean verified = phoneVerificationService.confirmCode(request.phone(), request.code());
        return new PhoneVerificationConfirmResponse(verified);
    }
}
