package com.golajugaenyang.auth.adapter.in.web;

import com.golajugaenyang.auth.application.AuthService;
import com.golajugaenyang.auth.application.recods.LoginCodePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token/exchange")
    public LoginCodePayload exchangeToken(@RequestBody TokenExchangeRequest request) {
        return authService.exchangeLoginCode(request.code());
    }
}
