package com.golajugaenyang.auth.adapter.in.web;

import com.golajugaenyang.auth.adapter.in.web.request.TokenExchangeRequest;
import com.golajugaenyang.auth.adapter.in.web.request.TokenRefreshRequest;
import com.golajugaenyang.auth.adapter.in.web.response.TokenRefreshResponse;
import com.golajugaenyang.auth.application.AuthService;
import com.golajugaenyang.auth.application.recods.LoginCodePayload;
import com.golajugaenyang.auth.application.recods.TokenPair;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token/exchange")
    public LoginCodePayload exchangeToken(@RequestBody TokenExchangeRequest request) {
        return authService.exchangeLoginCode(request.code());
    }

    @PostMapping("/token/refresh")
    public TokenRefreshResponse reissueTokenSet(
            @Valid @RequestBody TokenRefreshRequest request) {
        TokenPair tokenPair = authService.refreshTokens(request.refreshToken());
        String accessToken = tokenPair.accessToken();
        String refreshToken = tokenPair.refreshToken();
        return new TokenRefreshResponse(accessToken, refreshToken);
    }
}
