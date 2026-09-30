package com.golajugaenyang.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;


public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final Map<String, String> PROMPT_BY_REGISTRATION_ID = Map.of(
        "google", "select_account",
        "kakao", "login"
    );

    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    public CustomAuthorizationRequestResolver(
        ClientRegistrationRepository clientRegistrationRepository,
        String authorizationRequestBaseUri
    ) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, authorizationRequestBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return applyPrompt(delegate.resolve(request));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return applyPrompt(delegate.resolve(request, clientRegistrationId));
    }

    private OAuth2AuthorizationRequest applyPrompt(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }
        String registrationId = (String) authorizationRequest.getAttributes()
            .get(OAuth2ParameterNames.REGISTRATION_ID);
        String prompt = PROMPT_BY_REGISTRATION_ID.get(registrationId);
        if (prompt == null) {
            return authorizationRequest;
        }
        Map<String, Object> additionalParameters = new LinkedHashMap<>(
            authorizationRequest.getAdditionalParameters());
        additionalParameters.put("prompt", prompt);
        return OAuth2AuthorizationRequest.from(authorizationRequest)
            .additionalParameters(additionalParameters)
            .build();
    }
}
