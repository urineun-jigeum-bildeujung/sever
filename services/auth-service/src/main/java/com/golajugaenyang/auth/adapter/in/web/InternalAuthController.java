package com.golajugaenyang.auth.adapter.in.web;

import com.golajugaenyang.auth.adapter.in.web.request.PhoneInternalConfirmRequest;
import com.golajugaenyang.auth.adapter.in.web.request.TokenReissueRequest;
import com.golajugaenyang.auth.adapter.in.web.response.PhoneInternalConfirmResponse;
import com.golajugaenyang.auth.application.AuthService;
import com.golajugaenyang.auth.application.PhoneVerificationService;
import com.golajugaenyang.auth.application.recods.TokenPair;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/v1/auths")
@RequiredArgsConstructor
public class InternalAuthController {

    private final AuthService authService;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/token/reissue")
    public TokenPair reissueToken(@RequestBody TokenReissueRequest request) {
        return authService.reissueTokens(request.authId(), request.memberId());
    }

    @PostMapping("/phone/verify-confirm")
    public PhoneInternalConfirmResponse verifyConfirm(
            @Valid @RequestBody PhoneInternalConfirmRequest request) {
        boolean verified = phoneVerificationService.confirmCode(request.phone(), request.code());
        return new PhoneInternalConfirmResponse(verified);
    }
}
