package com.golajugaenyang.auth.adapter.in.web;

import com.golajugaenyang.auth.adapter.in.web.request.MemberMyEmailRequest;
import com.golajugaenyang.auth.adapter.in.web.request.MemberWithdrawRequest;
import com.golajugaenyang.auth.adapter.in.web.request.PhoneInternalConfirmRequest;
import com.golajugaenyang.auth.adapter.in.web.request.TokenInternalReissueRequest;
import com.golajugaenyang.auth.adapter.in.web.response.MemberMyEmailResponse;
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
    public TokenPair reissueToken(@Valid @RequestBody TokenInternalReissueRequest request) {
        return authService.reissueTokens(request.authId(), request.memberId());
    }

    @PostMapping("/phone/verify-confirm")
    public PhoneInternalConfirmResponse verifyConfirm(
            @Valid @RequestBody PhoneInternalConfirmRequest request) {
        boolean verified = phoneVerificationService.confirmCode(request.phone(), request.code());
        return new PhoneInternalConfirmResponse(verified);
    }

    @PostMapping("/member/me/email")
    public MemberMyEmailResponse getMyEmail(@Valid @RequestBody MemberMyEmailRequest request) {
        String email = authService.getMyEmail(request.authId());
        return new MemberMyEmailResponse(email);
    }

    @PostMapping("/member/withdraw")
    public void withdraw(@Valid @RequestBody MemberWithdrawRequest request) {
        authService.withdraw(request.authId(), request.accessToken());
    }
}
