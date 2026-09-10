package com.golajugaenyang.auth.application;

import com.golajugaenyang.auth.domain.entity.Auth;
import com.golajugaenyang.auth.domain.entity.enums.AuthStatus;
import com.golajugaenyang.auth.domain.repository.AuthRepository;
import com.golajugaenyang.auth.security.jwt.JwtIssuer;
import com.golajugaenyang.auth.security.jwt.RefreshTokenStore;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtIssuer jwtIssuer;
    private final RefreshTokenStore refreshTokenStore;

    public AuthLoginResult findOrCreateAuth(String provider, String socialId, String socialEmail) {
        Optional<Auth> existingAuth = authRepository.findByProviderAndSocialId(provider, socialId);
        boolean isNewUser = existingAuth.isEmpty();
        Auth auth = existingAuth.orElseGet(() -> authRepository.save(
            new Auth(null, provider, socialId, socialEmail, AuthStatus.ACTIVE, null, null)
        ));
        return new AuthLoginResult(auth, isNewUser);
    }

    public TokenPair issueTokens(Long authId) {
        String accessToken = jwtIssuer.generateAccessToken(authId);
        String refreshToken = jwtIssuer.generateRefreshToken(authId);
        refreshTokenStore.save(authId, refreshToken, jwtIssuer.getRefreshTokenExpirationSeconds());
        return new TokenPair(accessToken, refreshToken);
    }
}
