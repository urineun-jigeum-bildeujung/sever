package com.golajugaenyang.auth.security;

import com.golajugaenyang.auth.application.AuthService;
import com.golajugaenyang.auth.application.recods.TokenPair;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${oauth2.frontend-redirect-uri}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long authId = oAuth2User.getAuth().getId();

        TokenPair tokenPair = authService.issueTokens(authId);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
            .queryParam("accessToken", tokenPair.accessToken())
            .queryParam("refreshToken", tokenPair.refreshToken())
            .queryParam("isNewUser", oAuth2User.isNewUser())
            .build()
            .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
