package com.golajugaenyang.auth.adapter.in.web;

import com.golajugaenyang.auth.adapter.in.web.request.TokenReissueRequest;
import com.golajugaenyang.auth.application.AuthService;
import com.golajugaenyang.auth.application.recods.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/auths")
@RequiredArgsConstructor
public class InternalAuthController {

    private final AuthService authService;

    @PostMapping("/token/reissue")
    public TokenPair reissueToken(@RequestBody TokenReissueRequest request) {
        return authService.reissueTokens(request.authId(), request.memberId());
    }
}
