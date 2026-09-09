package com.golajugaenyang.auth.security;

import com.golajugaenyang.auth.domain.entity.Auth;
import com.golajugaenyang.auth.domain.entity.enums.AuthStatus;
import com.golajugaenyang.auth.domain.repository.AuthRepository;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final AuthRepository authRepository;

    CustomOAuth2UserService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String socialId;
        String socialEmail;
        String nameAttributeKey;

        if (registrationId.equals("kakao")) {
            socialId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            socialEmail = (String) kakaoAccount.get("email");
            nameAttributeKey = "id";
        } else {
            socialId = String.valueOf(attributes.get("sub"));
            socialEmail = (String) attributes.get("email");
            nameAttributeKey = "sub";
        }

        Optional<Auth> existingAuth = authRepository.findByProviderAndSocialId(registrationId, socialId);
        boolean isNewUser = existingAuth.isEmpty();
        Auth auth = existingAuth.orElseGet(() -> authRepository.save(
            new Auth(null, registrationId, socialId, socialEmail, AuthStatus.ACTIVE, null, null)
        ));

        return new CustomOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            auth,
            isNewUser,
            nameAttributeKey
        );
    }
}
