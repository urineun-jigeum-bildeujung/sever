package com.golajugaenyang.auth.adapter.in.web;

import com.golajugaenyang.auth.security.jwt.JwtKeyProvider;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class JwkSetController {

    private final JwtKeyProvider jwtKeyProvider;

    @GetMapping("/jwks")
    public Map<String, Object> getJwks() {
        return jwtKeyProvider.getPublicJWKSet().toJSONObject();
    }
}
